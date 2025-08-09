    package com.example.devdash.controller.cards.GitHub;

    import com.example.devdash.controller.cards.DashboardCard;
    import com.example.devdash.helper.Session;
    import com.example.devdash.model.Commit;
    import com.example.devdash.model.TaskModel;
    import com.example.devdash.model.User;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.VBox;
    import org.kohsuke.github.*;

    import java.io.IOException;
    import java.time.Instant;
    import java.util.ArrayList;

    /**
     * Controller for the GitHub card in the dashboard.
     *
     * Author: Alexander Sukhin
     * Version: 04/08/2025
     */
    public class GitHubCardController implements DashboardCard {

        @FXML private GridPane rootGridPane;
        @FXML private VBox commitsContainer;
        private GitHub github;

        @FXML
        public void initialize() throws RuntimeException {
            new Thread(() -> {
                try {
                    github = GitHub.connectAnonymously();
                    GHUser user = github.getUser("alexsukhin");

                    PagedIterable<GHEventInfo> events = user.listEvents();

                    for (GHEventInfo event : events) {
                        if ("PUSH".equalsIgnoreCase(String.valueOf(event.getType()))) {
                            GHEventPayload.Push pushPayload = event.getPayload(GHEventPayload.Push.class);
                            pushPayload.getCommits().forEach(pushCommit -> {

                                Commit commit = new Commit(
                                        pushCommit.getSha(),
                                        pushCommit.getMessage(),
                                        pushCommit.getUrl().toString()
                                );

                                Platform.runLater(() -> addCommitUI(commit));

                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        private void addCommitUI(Commit commit) {
            Label commitLabel = new Label(commit.toString() + " (" + commit.getFormattedCommittedAt() + ")");
            commitLabel.setStyle("-fx-font-size: 14px;"); // You can adjust this to 16, 18, etc.
            commitsContainer.getChildren().add(commitLabel);
        }


        /**
         * Returns the root UI node for this card.
         *
         * @return The root VBox node of this card
         */
        public Node getView() {
            return rootGridPane;
        }
    }
