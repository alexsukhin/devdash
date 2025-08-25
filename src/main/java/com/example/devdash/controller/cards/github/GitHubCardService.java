package com.example.devdash.controller.cards.github;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.github.Commit;
import com.example.devdash.model.github.GitHubService;
import com.example.devdash.model.auth.LoginModel;
import javafx.application.Platform;
import org.kohsuke.github.GHRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for GitHubCardController.
 * Handles linking, unlinking, fetching repositories, commits,
 * and caching daily commit counts.
 *
 * Author: Alexander Sukhin
 * Version: 25/08/2025
 */
public class GitHubCardService {

    private GitHubService gitHubService;
    private final LoginModel loginModel;
    private final int userID;
    private String ghUsername;
    private Map<LocalDate, Integer> cachedDailyCounts = new HashMap<>();

    /**
     * Constructor initializes service with login model, user ID, and access token.
     *
     * @param loginModel LoginModel for database access
     * @param userID     User ID
     * @param accessToken GitHub PAT
     */
    public GitHubCardService(LoginModel loginModel, int userID, String accessToken) {
        this.loginModel = loginModel;
        this.userID = userID;
        initializeService(accessToken);
    }

    /**
     * Initializes the GitHubService with the provided access token.
     *
     * @param accessToken GitHub PAT
     */
    private void initializeService(String accessToken) {
        try {
            gitHubService = new GitHubService(accessToken);
            ghUsername = gitHubService.getGhUsername();
        } catch (IOException e) {
            e.printStackTrace();
            gitHubService = null;
            ghUsername = null;
        }
    }

    /**
     * Returns the linked GitHub username.
     *
     * @return GitHub username if linked, null otherwise
     */
    public String getGhUsername() {
        return ghUsername;
    }

    /**
     * Checks whether the GitHub account is linked.
     *
     * @return True if linked, false otherwise
     */
    public boolean isLinked() {
        return gitHubService != null && ghUsername != null;
    }

    /**
     * Links the user's GitHub account by saving the access token in the session and database.
     * Initializes the GitHubService.
     *
     * @param accessToken GitHub pat
     */
    public void link(String accessToken) {
        Session.getInstance().getUser().setAccessToken(accessToken);
        loginModel.setGitHubAccessToken(accessToken, userID);
        initializeService(accessToken);
    }


    /**
     * Unlinks the user's GitHub account by clearing the session, database, and cached data.
     */
    public void unlink() {
        Session.getInstance().getUser().setAccessToken(null);
        loginModel.setGitHubAccessToken(null, userID);
        gitHubService = null;
        ghUsername = null;
        cachedDailyCounts.clear();
    }

    /**
     * Fetches all repositories accessible to the user and passes them to the controller.
     *
     * @param controller GitHubCardController to receive repository map
     */
    public void fetchRepositories(GitHubCardController controller) {
        if (gitHubService == null) return;

        new Thread(() -> {
            try {
                Map<String, GHRepository> repos = gitHubService.getRepositories();
                Platform.runLater(() -> {
                    controller.setRepositories(repos);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Fetches commits and daily commit counts for the given repository and date range.
     * Updates the controller with commits and heatmap.
     *
     * @param repo Repo full name user/repo or "All Repositories"
     * @param start Start date. Can be null
     * @param end End date. Can be null
     * @param controller GitHubCardController to update commits and heatmap
     */
    public void fetchCommits(String repo, LocalDate start, LocalDate end, GitHubCardController controller) {
        if (gitHubService == null) return;

        new Thread(() -> {
            try {
                List<Commit> commits;
                Map<LocalDate, Integer> dailyCounts;

                if (repo == null || repo.equals("All Repositories")) {
                    commits = gitHubService.fetchCommits(null, start, end, 50);
                    dailyCounts = gitHubService.fetchDailyCommitCounts(null, start, end, 5000);
                } else {
                    commits = gitHubService.fetchCommits(repo, start, end, 50);
                    dailyCounts = gitHubService.fetchDailyCommitCounts(repo, start, end, 5000);
                }

                cachedDailyCounts = dailyCounts;

                Platform.runLater(() -> {
                    controller.setCommits(commits);
                    controller.populateHeatMapForYear(LocalDate.now().getYear());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Returns cached daily commit counts.
     *
     * @return Map of LocalDate to number of commits
     */
    public Map<LocalDate, Integer> getCachedDailyCounts() {
        return cachedDailyCounts;
    }
}