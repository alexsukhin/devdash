package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.auth.User;
import com.example.devdash.model.typingtest.TypingSession;
import com.example.devdash.model.typingtest.TypingTestModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class LeaderboardController implements TypingTestPaneController {

    @FXML private VBox sessionsContainer;

    private TypingTestModel typingModel;
    private int userId;


    /**
     * Called automatically after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        User user = Session.getInstance().getUser();

        if (user != null) {
            userId = user.getID();
            typingModel = new TypingTestModel();
            resetPane();
        }
    }

    private void loadSessions() {
        if (typingModel == null) return;

        List<TypingSession> sessions = typingModel.getQuickestSessions();
        sessionsContainer.getChildren().clear();

        for (int i = 0; i < sessions.size(); i++) {
            TypingSession session = sessions.get(i);
            HBox sessionBox = createSessionNode(i + 1, session); // Pass the rank/index
            if ((i + 1) % 2 == 1) {
                sessionBox.getStyleClass().add("session-box");
            }
            sessionsContainer.getChildren().add(sessionBox);
        }
    }

    private HBox createSessionNode(int rank, TypingSession session) {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setMaxWidth(500); // Same as your VBox maxWidth
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Labels with exact prefWidths
        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.setPrefWidth(30);
        rankLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label nameLabel = new Label(session.getUsername());
        nameLabel.setPrefWidth(220);

        Label wpmLabel = new Label(String.format("%.1f", session.getWpm()));
        wpmLabel.setPrefWidth(50);
        wpmLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label accuracyLabel = new Label(String.format("%.1f%%", session.getAccuracy()));
        accuracyLabel.setPrefWidth(100);
        accuracyLabel.setAlignment(javafx.geometry.Pos.CENTER);

        VBox dateBox = new VBox();
        dateBox.setPrefWidth(100);
        dateBox.setAlignment(javafx.geometry.Pos.CENTER);
        dateBox.getStyleClass().add("font-time");
        Label dateLabel = new Label(session.getStartTime().split(" ")[0]); // show only date
        Label dateTimeLabel = new Label(session.getStartTime().split(" ")[1]);

        dateBox.getChildren().addAll(dateLabel, dateTimeLabel);
        hbox.getChildren().addAll(rankLabel, nameLabel, wpmLabel, accuracyLabel, dateBox);
        hbox.setMinHeight(40);
        return hbox;
    }


    @Override
    public void resetPane() {
        loadSessions();
    }
}


