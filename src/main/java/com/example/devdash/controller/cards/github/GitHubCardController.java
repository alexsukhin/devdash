package com.example.devdash.controller.cards.github;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.data.Session;
import com.example.devdash.model.github.Commit;
import com.example.devdash.model.auth.LoginModel;
import com.example.devdash.model.auth.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kohsuke.github.GHRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for the GitHub card in the dashboard.
 * Handles UI events, FXML bindings, and delegates API calls to GitHubCardService.
 *
 * Author: Alexander Sukhin
 * Version: 25/08/2025
 */
public class GitHubCardController implements DashboardCard {

    @FXML private Node rootNode;
    @FXML private VBox commitsContainer;
    @FXML private Label linkText;
    @FXML private Button linkButton;
    @FXML private GridPane heatmapGridPane;
    @FXML private VBox yearButtonsContainer;
    @FXML private ComboBox<String> repoSelected;
    @FXML private DatePicker startSelected;
    @FXML private DatePicker endSelected;

    private GitHubCardService githubService;
    private CommitHeatmap heatmap;
    private YearButtonsManager yearButtonsManager;


    /**
     * Initializes the controller after FXML is loaded.
     * Sets up GitHub service, heatmap, and year button manager.
     */
    @FXML
    public void initialize() {
        User user = Session.getInstance().getUser();
        if (user == null) return;

        LoginModel loginModel = new LoginModel();
        int userID = user.getID();

        githubService = new GitHubCardService(loginModel, userID, user.getAccessToken());
        heatmap = new CommitHeatmap(heatmapGridPane);
        yearButtonsManager = new YearButtonsManager(yearButtonsContainer, this);

        updateUI();
    }

    /**
     * Updates all UI components based on GitHub link status.
     * Shows linked username, updates repository list, year buttons, and commits.
     */
    private void updateUI() {
        commitsContainer.getChildren().clear();
        heatmap.clear();
        yearButtonsManager.clear();

        if (githubService.isLinked()) {
            linkText.setText("Linked as " + githubService.getGhUsername());
            linkButton.setText("[Unlink Github]");
            githubService.fetchRepositories(this);
            yearButtonsManager.populateYearButtons();
            applyFilter();
        } else {
            linkText.setText("Not Linked");
            linkButton.setText("[Link Github]");
        }

        configureLinkButton();
    }

    /**
     * Configures the GitHub link/unlink button action.
     */
    private void configureLinkButton() {
        linkButton.setOnAction(e -> {
            if (githubService.isLinked()) {
                githubService.unlink();
                updateUI();
            } else linkUser();
        });
    }

    /**
     * Shows a dialog to input Personal Access Token for linking GitHub.
     * On successful input, links the account and updates the UI.
     */
    private void linkUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Link GitHub");
        dialog.setHeaderText("Enter your GitHub PAT:");
        dialog.setContentText("PAT:");
        dialog.showAndWait().ifPresent(token -> {
            if (!token.isBlank()) {
                githubService.link(token);
                updateUI();
            }
        });
    }


    /**
     * Sets repositories in the repository selection ComboBox.
     *
     * @param repos Map of repository full names to GHRepository objects
     */
    public void setRepositories(Map<String, GHRepository> repos) {
        repoSelected.getItems().clear();
        repoSelected.getItems().add("All Repositories");
        for (GHRepository r : repos.values()) repoSelected.getItems().add(r.getFullName());
        repoSelected.getSelectionModel().selectFirst();
    }

    /**
     * Applies commit filter based on selected repository and date range.
     * Called when clicks apply button.
     */
    @FXML
    private void applyFilter() {
        String repo = repoSelected.getValue();
        LocalDate start = startSelected.getValue();
        LocalDate end = endSelected.getValue();
        githubService.fetchCommits(repo, start, end, this);
    }


    /**
     * Updates the commits container with a list of commits.
     *
     * @param commits List of Commit objects to display.
     */
    public void setCommits(List<Commit> commits) {
        commitsContainer.getChildren().clear();
        for (Commit c : commits) {
            Label l = new Label(c.toString() + " (" + c.getFormattedCommittedAt() + ")");
            l.setStyle("-fx-font-size: 14px;");
            commitsContainer.getChildren().add(l);
        }
    }

    /**
     * Populates the heatmap for a given year using cached daily commit counts.
     *
     * @param year Year to display on the heatmap.
     */
    public void populateHeatMapForYear(int year) {
        Map<LocalDate, Integer> cachedCounts = githubService.getCachedDailyCounts();
        if (cachedCounts == null) return;

        Map<LocalDate, Integer> yearCounts = new java.util.HashMap<>();
        for (Map.Entry<LocalDate, Integer> e : cachedCounts.entrySet()) {
            if (e.getKey().getYear() == year) yearCounts.put(e.getKey(), e.getValue());
        }

        heatmap.populateHeatMapFullYear(yearCounts, year);
    }

    /**
     * Returns the root UI node for this card.
     *
     * @return The root node of this card
     */
    @Override
    public Node getView() {
        return rootNode;
    }
}