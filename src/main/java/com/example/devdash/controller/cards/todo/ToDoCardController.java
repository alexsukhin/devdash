package com.example.devdash.controller.cards.todo;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.data.Session;
import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import com.example.devdash.model.auth.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller for the To-do card in the dashboard.
 *
 * Manages three task columns (TODO, IN_PROGRESS, DONE) and allows
 * adding, deleting, and dragging tasks between columns.
 * Interacts with TaskModel for database operations.
 *
 * Author: Alexander Sukhin
 * Version: 30/08/2025
 */
public class ToDoCardController implements DashboardCard {

    @FXML private Node rootNode;

    @FXML private VBox todoTasks;
    @FXML private VBox inProgressTasks;
    @FXML private VBox doneTasks;

    @FXML private Button addTodoCard;
    @FXML private Button addProgressCard;
    @FXML private Button addDoneCard;

    @FXML private ScrollPane todoScrollPane;

    private TaskModel taskModel;
    private int userId;

    private TaskNodeFactory taskNodeFactory;
    private DragAndDropHandler dragHandler;


    /**
     * Initializes the controller after the FXML is loaded.
     * Retrieves current user, initializes model and factories,
     * loads tasks from DB, and sets up add task buttons and drag-and-drop.
     *
     */
    @FXML
    public void initialize() {
        User user = Session.getInstance().getUser();
        if (user == null) return;

        userId = user.getID();
        taskModel = new TaskModel();

        taskNodeFactory = new TaskNodeFactory(taskModel, this::loadTasksFromDb);
        dragHandler = new DragAndDropHandler(taskModel, this::loadTasksFromDb);

        loadTasksFromDb();

        addTodoCard.setOnAction(e -> showAddTaskDialog("TODO"));
        addProgressCard.setOnAction(e -> showAddTaskDialog("IN_PROGRESS"));
        addDoneCard.setOnAction(e -> showAddTaskDialog("DONE"));

        dragHandler.setupDragAndDrop(todoTasks, "TODO");
        dragHandler.setupDragAndDrop(inProgressTasks, "IN_PROGRESS");
        dragHandler.setupDragAndDrop(doneTasks, "DONE");
    }

    /**
     * Shows a modal dialog to input a new task and adds it to the DB.
     *
     * @param status Status column for the new task ("TODO", "IN_PROGRESS", "DONE")
     */
    private void showAddTaskDialog(String status) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Enter task details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField descField = new TextField();
        descField.setPromptText("Task description");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.getSelectionModel().select(0);

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date");

        VBox content = new VBox(10,
                new Label("Description:"), descField,
                new Label("Priority:"), priorityBox,
                new Label("Due Date:"), dueDatePicker
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String description = descField.getText().trim();
                int priority = priorityBox.getSelectionModel().getSelectedIndex();
                String dueDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : null;

                if (!description.isBlank()) {
                    boolean success = taskModel.addTask(userId, description, status, priority, dueDate);
                    if (success) loadTasksFromDb();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Loads all tasks for the current user and displays them in their respective columns.
     * Clears existing nodes before adding new task nodes created by TaskNodeFactory.
     */
    private void loadTasksFromDb() {
        List<Task> tasks = taskModel.getTasksForUser(userId);

        todoTasks.getChildren().clear();
        inProgressTasks.getChildren().clear();
        doneTasks.getChildren().clear();

        for (Task task : tasks) {
            Node taskNode = taskNodeFactory.createTaskNode(task);
            switch (task.getStatus()) {
                case "TODO" -> todoTasks.getChildren().add(taskNode);
                case "IN_PROGRESS" -> inProgressTasks.getChildren().add(taskNode);
                case "DONE" -> doneTasks.getChildren().add(taskNode);
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
