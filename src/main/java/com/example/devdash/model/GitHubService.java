package com.example.devdash.model;

import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Service class responsible for communicating with the GitHub API.
 */
public class GitHubService {

    private GitHub github;
    private String ghUsername;

    /**
     * Constructor that sets up the GitHub API client.
     * If a Personal Access Token (PAT) is provided, it authenticates with it.
     *
     * @param accessToken GitHub Personal Access Token (PAT), can be null for anonymous access.
     */
    public GitHubService(String accessToken) throws IOException {
        if (accessToken != null && !accessToken.isBlank()) {
            github = GitHub.connectUsingOAuth(accessToken);
        }
    }

    /**
     * Commit processor: traverses push events and executes a callback
     * for each GHCommit with maxCommits limit.
     *
     * @param maxCommits Max commits to fetch
     * @param commitConsumer Action to perform on each commit
     */
    private void processCommits(int maxCommits, Consumer<GHCommit> commitConsumer) throws IOException, InterruptedException {
        if (github == null) return;

        AtomicInteger count = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        GHMyself me = github.getMyself();
        ghUsername = me.getLogin();
        PagedIterable<GHEventInfo> events = me.listEvents().withPageSize(50);

        for (GHEventInfo event : events) {
            if (count.get() >= maxCommits) break;
            if (!"PUSH".equalsIgnoreCase(String.valueOf(event.getType()))) continue;

            GHEventPayload.Push pushPayload = event.getPayload(GHEventPayload.Push.class);
            GHRepository repo = event.getRepository();

            for (GHEventPayload.Push.PushCommit commit : pushPayload.getCommits()) {
                if (count.get() >= maxCommits) break;

                executor.submit(() -> {
                    try {
                        GHCommit ghCommit = repo.getCommit(commit.getSha());
                        commitConsumer.accept(ghCommit);
                        count.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }

    /**
     * Fetches recent commits as a list of commit objects.
     *
     * @param maxCommits The maximum number of commits to retrieve.
     * @return A list of Commit model objects containing commit details.
     */
    public List<Commit> fetchCommits(int maxCommits) throws IOException, InterruptedException {
        List<Commit> commits = Collections.synchronizedList(new ArrayList<>());

        processCommits(maxCommits, ghCommit -> {
            try {
                LocalDateTime commitedAt = LocalDateTime.ofInstant(
                        ghCommit.getCommitDate().toInstant(),
                        ZoneId.systemDefault()
                );

                commits.add(new Commit(
                        ghCommit.getSHA1(),
                        ghCommit.getCommitShortInfo().getMessage(),
                        ghCommit.getHtmlUrl().toString(),
                        commitedAt
                ));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return commits;
    }


    /**
     * Fetch daily commit counts for the last 12 weeks.
     *
     * @param maxCommits maximum commits to fetch
     * @return map of LocalDate -> number of commits
     */
    public Map<LocalDate, Integer> fetchDailyCommitCounts(int maxCommits) throws IOException, InterruptedException {
        Map<LocalDate, Integer> dailyCounts = new HashMap<>();
        LocalDate threeMonthsAgo = LocalDate.now().minusWeeks(12);

        processCommits(maxCommits, ghCommit -> {
            LocalDate commitDate = null;
            try {
                commitDate = LocalDateTime.ofInstant(
                        ghCommit.getCommitDate().toInstant(),
                        ZoneId.systemDefault()
                ).toLocalDate();

                if (!commitDate.isBefore(threeMonthsAgo)) {
                    synchronized (dailyCounts) {
                        dailyCounts.put(commitDate, dailyCounts.getOrDefault(commitDate, 0) + 1);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        return dailyCounts;
    }

    /**
     * @return The user's GitHub username
     */
    public String getGhUsername() {
        return ghUsername;
    }
}
