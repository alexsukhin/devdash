package com.example.devdash.model.github;

import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Service class responsible for communicating with the GitHub API.
 * Provides functionality to fetch commits, repositories,
 * and commit statistics for a user.
 *
 * Author: Alexander Sukhin
 * Version: 24/08/2025
 */
public class GitHubService {

    private final GitHub github;
    private final String ghUsername;

    /**
     * Constructor that sets up the GitHub API client.
     * If a Personal Access Token (PAT) is provided, it authenticates with it.
     *
     * @param accessToken GitHub Personal Access Token (PAT), can be null for anonymous access.
     * @throws IOException If authentication or connection to GitHub fails.
     */
    public GitHubService(String accessToken) throws IOException {
        if (accessToken != null && !accessToken.isBlank()) {
            github = GitHub.connectUsingOAuth(accessToken);
            ghUsername = github.getMyself().getLogin();
        }  else {
            this.github = GitHub.connectAnonymously();
            this.ghUsername = null; // anonymous mode
        }
    }


    /**
     * Internal method to fetch  GH commits from GitHub repositories
     * Commits are filtered by date range and limited by limit.
     *
     * @param repoFullName Repo full name user/repo
     * @param start        Start date. Can be null
     * @param end          End date. Can be null
     * @param limit        Maximum number of commits to fetch.
     * @return List of GHCommit objects.
     * @throws IOException If repository access fails.
     */
    private List<GHCommit> retrieveCommits(String repoFullName,
                                           LocalDate start,
                                           LocalDate end, int limit) throws IOException {
        Collection<GHRepository> repos = (repoFullName == null || repoFullName.isBlank())
                ? getRepositories().values()
                : Collections.singletonList(github.getRepository(repoFullName));


        List<GHCommit> results = new ArrayList<>();
        for (GHRepository repo : repos) {
            for (GHCommit c : repo.listCommits()) {
                if (results.size() >= limit) break;

                try {
                    LocalDate date = toLocalDate(c);

                    boolean inRange = (start == null || !date.isBefore(start))
                            && (end == null || !date.isAfter(end));

                    if (inRange) results.add(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    /**
     * Converts GHCommit objects into Commit objects
     *
     * @param commits List of GHCommit objects.
     * @return List of Commit objects.
     */
    private List<Commit> toCommits(List<GHCommit> commits) {
        List<Commit> results = new ArrayList<>();
        for (GHCommit c : commits) {
            try {
                results.add(new Commit(
                        c.getSHA1(),
                        c.getCommitShortInfo().getMessage(),
                        c.getHtmlUrl().toString(),
                        LocalDateTime.ofInstant(c.getCommitDate().toInstant(), ZoneId.systemDefault())
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * Aggregates GHCommit objects into daily commit counts.
     *
     * @param commits List of GHCommit objects
     * @return Map of commit counts per LocalDate
     */
    private Map<LocalDate, Integer> toDailyCounts(List<GHCommit> commits) {
        Map<LocalDate, Integer> counts = new HashMap<>();
        for (GHCommit c : commits) {
            try {
                LocalDate date = toLocalDate(c);
                counts.put(date, counts.getOrDefault(date, 0) + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return counts;
    }


    /**
     * Fetches detailed commits
     *
     * @param repoFullName Repo full name user/repo. Can be null
     * @param start        Start date. Can be null
     * @param end          End date. Can be null
     * @param limit        Maximum number of commits to fetch.
     * @return List of Commit objects.
     * @throws IOException If GitHub API access fails.
     */
    public List<Commit> fetchCommits(String repoFullName, LocalDate start, LocalDate end, int limit) throws IOException {
        return toCommits(retrieveCommits(repoFullName, start, end, limit));
    }


    /**
     * Fetches aggregated daily commit counts
     *
     * @param repoFullName Repo full name user/repo. Can be null
     * @param start        Start date. Can be null
     * @param end          End date. Can be null
     * @param limit        Maximum number of commits to fetch.
     * @return List of Commit objects.
     * @throws IOException If GitHub API access fails.
     */
    public Map<LocalDate, Integer> fetchDailyCommitCounts(String repoFullName, LocalDate start, LocalDate end, int limit) throws IOException {
        return toDailyCounts(retrieveCommits(repoFullName, start, end, limit));
    }


    /**
     * Converts a GHCommit into a LocalDate.
     *
     * @param commit GitHub commit
     * @return Commit date
     * @throws IOException If commit metadata cannot be accessed
     */
    private LocalDate toLocalDate(GHCommit commit) throws IOException {
        return commit.getCommitDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * @return The user's GitHub username
     */
    public String getGhUsername() {
        return ghUsername;
    }


    /**
     * Fetches all repositories accessible to the authenticated user.
     *
     * @return Map of repository names to GHRepository objects
     * @throws IOException If repository access fails.
     */
    public Map<String, GHRepository> getRepositories() throws IOException {
        if (github == null) return Collections.emptyMap();
        return github.getMyself().getAllRepositories();
    }
}
