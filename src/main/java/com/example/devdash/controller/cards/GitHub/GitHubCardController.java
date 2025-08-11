    package com.example.devdash.controller.cards.GitHub;

    import com.example.devdash.controller.cards.DashboardCard;
    import com.example.devdash.helper.Session;
    import com.example.devdash.model.Commit;
    import com.example.devdash.model.LoginModel;
    import com.example.devdash.model.User;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextInputDialog;
    import javafx.scene.layout.VBox;
    import org.kohsuke.github.*;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.atomic.AtomicInteger;

    /**
     * Controller for the GitHub card in the dashboard.
     *
     * Author: Alexander Sukhin
     * Version: 04/08/2025
     */
    public class GitHubCardController implements DashboardCard {

        @FXML private Node rootNode;
        @FXML private VBox commitsContainer;
        @FXML private Label linkText;
        @FXML private Button linkButton;

        private GitHub github;
        private LoginModel loginModel;
        private boolean isLinked;
        private int userID;

        /**
         * Called automatically after the FXML file is loaded.
         * Initializes the card UI based on the current user's GitHub username.
         */
        @FXML
        public void initialize() throws RuntimeException {

            User user = Session.getInstance().getUser();

            if (user != null) {
                loginModel = new LoginModel();
                userID = user.getID();
                updateUI(user.getGhUsername());
            } else {
                System.err.println("User not logged in; GitHub card won't load.");
            }
        }

        /**
         * Triggered when user wants to link their GitHub username.
         * Prompts a dialog to enter the username, saves it in session and DB,
         * and updates the UI and commits list.
         */
        @FXML
        private void linkUser() {
            // Prompt user for their GitHub username
            TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Link GitHub");
            dialog.setHeaderText("Enter your GitHub username:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(username -> {
                if (!username.trim().isEmpty()) {
                    Session.getInstance().getUser().setGhUsername(username);
                    loginModel.setGitHubUsername(username, userID);
                    updateUI(username);
                }
            });
        }

        /**
         * Triggered when user wants to unlink their GitHub username.
         * Clears username from session and database,
         * then resets the UI accordingly.
         */
        @FXML
        private void unlinkUser() {
            Session.getInstance().getUser().setGhUsername(null);
            loginModel.setGitHubUsername(null, userID);
            updateUI(null);
        }

        /**
         * Updates the UI components based on the current GitHub username.
         *
         * @param ghUsername GitHub username, or null if unlinked
         */
        private void updateUI(String ghUsername) {
            commitsContainer.getChildren().clear();

            if (ghUsername != null && !ghUsername.trim().isEmpty()) {
                linkText.setText("Linked as " + ghUsername);
                linkButton.setText("[Unlink Github]");
                isLinked = true;

                configureLinkButton();
                loadCommits(ghUsername);
            } else {
                linkText.setText("Not Linked");
                linkButton.setText("[Link Github]");
                isLinked = false;

                configureLinkButton();
            }
        }

        /**
         * Configures the link button's onAction event based on the
         * current linked status.
         */
        private void configureLinkButton() {
            linkButton.setOnAction(e -> {
                if (isLinked) unlinkUser();
                else linkUser();
            });
        }

        /**
         * Loads the recent GitHub commits of the specified username asynchronously.
         * Fetches up to maxCommits from user's push events and updates the UI.
         *
         * @param ghUsername GitHub username, or null if unlinked
         */
        private void loadCommits(String ghUsername) {
            new Thread(() -> {
                try {
                    github = GitHub.connectAnonymously();

                    GHRateLimit rateLimit = github.getRateLimit();
                    System.out.println("Remaining API requests: " + rateLimit.getRemaining());
                    System.out.println("Rate limit resets at: " + rateLimit.getResetDate());

                    GHUser ghUser = github.getUser(ghUsername);
                    PagedIterable<GHEventInfo> events = ghUser.listEvents();

                    List<Commit> commits  = new ArrayList<>();
                    int maxCommits = 20; // max limit
                    AtomicInteger count = new AtomicInteger();

                    for (GHEventInfo event : events) {
                        System.out.println(count.get());
                        if (count.get() >= maxCommits) break;


                        if ("PUSH".equalsIgnoreCase(String.valueOf(event.getType()))) {
                            GHEventPayload.Push pushPayload = event.getPayload(GHEventPayload.Push.class);
                            GHRepository repo = event.getRepository();

                            pushPayload.getCommits().forEach(pushCommit -> {
                                try {
                                    GHCommit ghCommit = repo.getCommit(pushCommit.getSha());
                                    LocalDateTime commitedAt = LocalDateTime.ofInstant(
                                            ghCommit.getCommitDate().toInstant(), ZoneId.systemDefault()
                                    );

                                    Commit commit = new Commit(
                                            pushCommit.getSha(),
                                            pushCommit.getMessage(),
                                            pushCommit.getUrl(),
                                            commitedAt
                                    );
                                    commits.add(commit);
                                    count.getAndIncrement();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        count.getAndIncrement();
                    }

                    Platform.runLater(() -> displayCommits(commits));

                } catch (GHIOException e) {
                    System.err.println("GitHub API rate limit exceeded. Try again later.");
                } catch (IOException e) {
                    System.err.println("IOException fetching commit details: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }

        /**
         * Updates the commitsContainer UI with commit information.
         *
         * @param commits List of commits to display
         */
        private void displayCommits(List<Commit> commits) {
            commitsContainer.getChildren().clear();
            for (Commit commit : commits) {
                Label commitLabel = new Label(commit.toString() + " (" + commit.getFormattedCommittedAt() + ")");
                commitLabel.setStyle("-fx-font-size: 14px;");
                commitsContainer.getChildren().add(commitLabel);
            }
        }

        /**
         * Returns the root UI node for this card.
         *
         * @return The root node of this card
         */
        public Node getView() {
            return rootNode;
        }
    }
