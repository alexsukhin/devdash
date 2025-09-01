package com.example.devdash.controller.cards.todo;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.data.Session;
import com.example.devdash.model.todo.Sprint;
import com.example.devdash.model.todo.SprintModel;
import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import com.example.devdash.model.auth.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the To-do card in the dashboard.
 *
 * Supports sprint selection, task creation, task editing, drag-and-drop
 * between columns, and sprint lifecycle (add/finish).
 *
 * Author: Alexander Sukhin
 * Version: 30/08/2025
 */
public class ToDoCardController implements DashboardCard {


    @FXML private StackPane rootNode;
    @FXML private ComboBox<Sprint> sprintComboBox;
    @FXML private Label sprintLabel;
    @FXML private HBox sprintContainer;
    @FXML private ScrollPane cardScrollPane;

    @FXML private VBox backlogTasks;
    @FXML private VBox todoTasks;
    @FXML private VBox inProgressTasks;
    @FXML private VBox doneTasks;

    @FXML private Button addBacklogCard;

    private int userId;
    private TaskModel taskModel;
    private SprintModel sprintModel;
    private TaskNodeFactory taskNodeFactory;
    private DragAndDropHandler dragHandler;


    /**
     * Initializes the controller after the FXML is loaded.
     * Retrieves current user, initializes model and factories,
     * loads tasks from DB, and sets up add task buttons and drag-and-drop.
     */
    @FXML
    public void initialize() {
        initUser();
        initModelsAndFactories();
        loadSprintOptions();
        setupEventHandlers();
        reloadUI();
        bindScrollPaneWidth();
    }

    /**
     * Retrieves current user from session and stores their ID.
     */
    private void initUser() {
        User user = Session.getInstance().getUser();
        if (user != null) userId = user.getID();
    }

    /**
     * Initializes database models and helper factories for tasks and sprints.
     */
    private void initModelsAndFactories() {
        taskModel = new TaskModel();
        sprintModel = new SprintModel();
        taskNodeFactory = new TaskNodeFactory(taskModel, this::reloadUI, task -> TaskDialog.showTaskDialog(task, sprintComboBox.getValue(), this::reloadUI));
        dragHandler = new DragAndDropHandler(taskModel, this::reloadUI, this::getSelectedSprintId);
    }

    /**
     * Configures event handlers for sprint selection, task creation,
     * and drag-and-drop across columns.
     */
    private void setupEventHandlers() {
        sprintComboBox.setOnAction(e -> onSprintSelected());
        addBacklogCard.setOnAction(e -> TaskDialog.showTaskDialog(null, sprintComboBox.getValue(), this::reloadUI));

        dragHandler.setupDragAndDrop(backlogTasks, "BACKLOG");
        dragHandler.setupDragAndDrop(todoTasks, "TODO");
        dragHandler.setupDragAndDrop(inProgressTasks, "IN_PROGRESS");
        dragHandler.setupDragAndDrop(doneTasks, "DONE");
    }

    /**
     * Ensures the scroll pane width adapts to parent container.
     */
    private void bindScrollPaneWidth() {
        cardScrollPane.prefWidthProperty().bind(rootNode.widthProperty());
        cardScrollPane.maxWidthProperty().bind(rootNode.widthProperty());
    }

    /**
     * Triggered when a sprint is selected from the dropdown.
     * Updates sprint board visibility and loaded tasks accordingly.
     */
    private void onSprintSelected() {
        Sprint selected = sprintComboBox.getValue();
        if (selected == null || selected.getId() == 0) {
            clearSprintBoard();
            sprintLabel.setText("No Sprint Selected");
            sprintContainer.setVisible(false);
        } else {
            loadSprintBoard(selected.getId());
            sprintLabel.setText(selected.getName());
            sprintContainer.setVisible(true);
        }
    }

    /**
     * Returns the ID of the currently selected sprint,
     * or 0 if no sprint is selected.
     *
     * @return sprint ID or 0
     */
    private int getSelectedSprintId() {
        Sprint selected = sprintComboBox.getValue();
        return selected != null ? selected.getId() : 0;
    }

    /**
     * Loads backlog tasks from DB and displays them in backlog column.
     */
    private void loadBacklog() {
        backlogTasks.getChildren().clear();
        taskModel.getBacklogTasks(userId).forEach(task ->
                backlogTasks.getChildren().add(taskNodeFactory.createTaskNode(task))
        );
    }

    /**
     * Populates sprint dropdown with user sprints.
     * Includes "No Sprint" as the default option.
     */
    private void loadSprintOptions() {
        sprintComboBox.getItems().clear();
        sprintComboBox.getItems().add(new Sprint(0, "No Sprint"));
        sprintComboBox.getItems().addAll(sprintModel.getSprintsForUser(userId));
        sprintComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Loads all tasks for the given sprint ID into the board,
     * sorting them into the correct status column.
     *
     * @param sprintId The sprint to load tasks for
     */
    private void loadSprintBoard(int sprintId) {
        clearSprintBoard();

        taskModel.getTasksForSprint(sprintId).forEach(task -> {
            Node node = taskNodeFactory.createTaskNode(task);
            switch (task.getStatus()) {
                case "TODO" -> todoTasks.getChildren().add(node);
                case "IN_PROGRESS" -> inProgressTasks.getChildren().add(node);
                case "DONE" -> doneTasks.getChildren().add(node);
            }
        });
    }

    /**
     * Clears all sprint task columns.
     */
    private void clearSprintBoard() {
        todoTasks.getChildren().clear();
        inProgressTasks.getChildren().clear();
        doneTasks.getChildren().clear();
    }

    /**
     * Reloads the entire To-do card UI.
     * Re-fetches backlog and updates sprint board.
     */
    private void reloadUI() {
        loadBacklog();
        onSprintSelected();
    }

    /**
     * Opens dialog to add a new sprint.
     */
    @FXML
    private void addSprint() {
        SprintDialog.showAddSprintDialog(userId, sprintModel, this::loadSprintOptions);
    }

    /**
     * Completes the currently active sprint.
     * - Prompts user for confirmation
     * - Moves non-DONE tasks back to backlog
     * - Deletes DONE tasks permanently
     * - Removes sprint from DB and refreshes UI
     */
    @FXML
    private void finishSprint() {
        Sprint selected = sprintComboBox.getValue();
        if (selected == null || selected.getId() == 0) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Finish Sprint");
        confirm.setHeaderText("Move non-done tasks back to backlog?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        taskModel.getTasksForSprint(selected.getId()).forEach(task -> {
            if (!task.getStatus().equals("DONE")) {
                taskModel.removeTaskFromSprint(task.getId());
                taskModel.updateTaskStatus(task.getId(), "BACKLOG");
                taskModel.updateTaskTimestamp(task.getId());
            } else {
                taskModel.deleteTask(task.getId());
            }
        });

        sprintModel.deleteSprint(selected.getId());
        loadSprintOptions();
        reloadUI();
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
