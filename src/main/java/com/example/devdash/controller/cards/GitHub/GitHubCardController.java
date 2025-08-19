    package com.example.devdash.controller.cards.GitHub;
    import com.example.devdash.controller.cards.DashboardCard;
    import com.example.devdash.helper.Session;
    import com.example.devdash.model.Commit;
    import com.example.devdash.model.GitHubService;
    import com.example.devdash.model.LoginModel;
    import com.example.devdash.model.User;
    import javafx.application.Platform;
    import javafx.beans.binding.Bindings;
    import javafx.beans.binding.DoubleBinding;
    import javafx.fxml.FXML;
    import javafx.geometry.HPos;
    import javafx.geometry.VPos;
    import javafx.scene.Node;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextInputDialog;
    import javafx.scene.control.Tooltip;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.Region;
    import javafx.scene.layout.VBox;
    import java.io.IOException;
    import java.time.DayOfWeek;
    import java.time.LocalDate;
    import java.util.List;
    import java.util.Map;

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
        @FXML private GridPane heatmapGridPane;

        private String ghUsername;
        private LoginModel loginModel;
        private boolean isLinked;
        private int userID;

        /**
         * Called automatically after the FXML file is loaded.
         * Initializes the card UI based on the current user's GitHub PAT.
         */
        @FXML
        public void initialize() {
            User user = Session.getInstance().getUser();

            if (user != null) {
                loginModel = new LoginModel();
                userID = user.getID();
                updateUI(user.getAccessToken());

            } else {
                System.err.println("User not logged in; GitHub card won't load.");
            }

        }

        /**
         * Triggered when the user wants to link their GitHub account.
         * Shows a dialog to enter PAT, saves it in session/DB, updates UI.
         */
        @FXML
        private void linkUser() {
            TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Link GitHub");
            dialog.setHeaderText("Enter your GitHub PAT:");
            dialog.setContentText("PAT:");

            dialog.showAndWait().ifPresent(accessToken -> {
                if (!accessToken.trim().isEmpty()) {
                    Session.getInstance().getUser().setAccessToken(accessToken);
                    loginModel.setGitHubAccessToken(accessToken, userID);
                    updateUI(accessToken);
                }
            });
        }


        /**
         * Triggered when the user wants to unlink their GitHub account.
         * Clears token from session/DB and updates UI.
         */
        @FXML
        private void unlinkUser() {
            Session.getInstance().getUser().setAccessToken(null);
            loginModel.setGitHubAccessToken(null, userID);
            updateUI(null);
        }

        /**
         * Updates the UI components based on the current GitHub token.
         * Shows "Loading..." when fetching commits.
         *
         * @param accessToken GitHub PAT, or null if unlinked
         */
        private void updateUI(String accessToken) {
            commitsContainer.getChildren().clear();
            heatmapGridPane.getChildren().clear();

            if (accessToken != null && !accessToken.trim().isEmpty()) {
                linkText.setText("Loading...");
                linkButton.setText("[Unlink Github]");
                isLinked = true;

                new Thread(() -> {
                    try {
                        GitHubService service = new GitHubService(accessToken);
                        List<Commit> commits = service.fetchCommits(20);
                        Map<LocalDate, Integer> dailyCounts = service.fetchDailyCommitCounts(500);
                        ghUsername = service.getGhUsername();

                        Platform.runLater(() -> {
                            linkText.setText("Linked as " + ghUsername);
                            displayCommits(commits);
                            populateHeatMap(dailyCounts);
                        });
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                linkText.setText("Not Linked");
                linkButton.setText("[Link Github]");
                isLinked = false;
            }

            configureLinkButton();
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
         * Populates the heatmap GridPane with daily commit counts.
         * Each cell's color intensity represents the number of commits for that day.
         *
         * @param dailyCommitCounts Map of LocalDate -> number of commits
         */
        private void populateHeatMap(Map<LocalDate, Integer> dailyCommitCounts) {
            heatmapGridPane.getChildren().clear();

            LocalDate startDate = LocalDate.now().minusWeeks(12);

            for (int week = 0; week < 12; week++) {
                for (int day = 0; day < 7; day++) {
                    LocalDate currentDate = startDate.plusWeeks(week).with(DayOfWeek.MONDAY).plusDays(day);

                    int count = dailyCommitCounts.getOrDefault(currentDate, 0);

                    Region cell = new Region();
                    cell.setPrefSize(20, 20);

                    String color;

                    if (count == 1) color = "#9be9a8";
                    else if (count == 2) color = "#40c463";
                    else if (count == 3) color = "#30a14e";
                    else color = "#216e39";

                    cell.getStyleClass().add("cell-background");


                    if (count >= 1) {
                        cell.setStyle("-fx-border-radius: 3px; -fx-background-radius: 3px; -fx-background-color: " + color + ";");
                    } else {
                        cell.setStyle("-fx-border-radius: 3px; -fx-background-radius: 3px;");
                    }

                    Tooltip tooltip = new Tooltip(currentDate + "\nCommits: " + count);
                    tooltip.setShowDelay(javafx.util.Duration.millis(100)); // appear after 100ms
                    Tooltip.install(cell, tooltip);

                    GridPane.setHalignment(cell, HPos.CENTER);
                    GridPane.setValignment(cell, VPos.CENTER);

                    heatmapGridPane.add(cell, week, day);
                }
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
