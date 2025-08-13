package com.example.devdash.controller.cards.GitHub;

import com.example.devdash.model.Commit;
import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
     * @throws IOException if authentication fails or network issues occur.
     */
    public GitHubService(String accessToken) throws IOException {
        if (accessToken != null && !accessToken.isBlank()) {
            github = GitHub.connectUsingOAuth(accessToken);
        }
    }

    /**
     * Fetches recent commits asynchronously using a thread pool.
     * Fetches commits from push events and updates the UI when done.
     *
     * @param maxCommits The maximum number of commits to retrieve.
     * @return A list of Commit model objects containing commit details.
     * @throws IOException          If a network or API error occurs.
     * @throws InterruptedException If the thread pool is interrupted while waiting for tasks to finish.
     */
    public List<Commit> fetchCommits(int maxCommits) throws IOException, InterruptedException {
        if (github == null) return List.of();

        List<Commit> commits = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        GHMyself me = github.getMyself();
        ghUsername = me.getLogin();
        PagedIterable<GHEventInfo> events = me.listEvents().withPageSize(50);

        for (GHEventInfo event : events) {
            if (count.get() >= maxCommits) break;
            if ("PUSH".equalsIgnoreCase(String.valueOf(event.getType()))) {
                processPushEvent(event, commits, count, maxCommits, executor);
            }
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        return commits;
    }


    /**
     * Processes a PUSH event by scheduling commit fetch tasks.
     *
     * @param event      The GitHub event representing a PUSH action.
     * @param commits    The shared list to store Commit objects.
     * @param count      Counter to track the total number of fetched commits.
     * @param maxCommits Maximum number of commits to retrieve overall.
     * @param executor   ExecutorService to submit commit-fetching tasks.
     * @throws IOException If there's an error reading the event or repository.
     */
    private void processPushEvent(GHEventInfo event, List<Commit> commits, AtomicInteger count, int maxCommits, ExecutorService executor) throws IOException {
        GHEventPayload.Push pushPayload = event.getPayload(GHEventPayload.Push.class);
        GHRepository repo = event.getRepository();

        pushPayload.getCommits().forEach(pushCommit -> {
            if (count.get() >= maxCommits) return;
            executor.submit(() -> fetchCommitDetails(repo, pushCommit, commits, count));
        });
    }

    /**
     * Fetches commit details from GitHub API and adds to commits list.
     *
     * @param repo      Repository of the commit
     * @param pushCommit Commit object from the event
     * @param commits    Shared commits list
     * @param count      Counter to enforce MAX_COMMITS
     */
    private void fetchCommitDetails(GHRepository repo, GHEventPayload.Push.PushCommit pushCommit, List<Commit> commits, AtomicInteger count) {
        try {
            GHCommit ghCommit = repo.getCommit(pushCommit.getSha());
            LocalDateTime committedAt = LocalDateTime.ofInstant(ghCommit.getCommitDate().toInstant(), ZoneId.systemDefault());

            synchronized (commits) {
                commits.add(new Commit(pushCommit.getSha(), pushCommit.getMessage(), pushCommit.getUrl(), committedAt));
                count.getAndIncrement();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The user's GitHub username
     */
    public String getGhUsername() {
        return ghUsername;
    }
}
