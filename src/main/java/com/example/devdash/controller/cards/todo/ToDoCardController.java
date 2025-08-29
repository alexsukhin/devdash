package com.example.devdash.controller.cards.todo;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.data.Session;
import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import com.example.devdash.model.auth.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.List;

/**
 * Controller for the To-do card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class ToDoCardController implements DashboardCard {

    @FXML private Node rootNode;

    @FXML private VBox todoTasks;
    @FXML private VBox inProgressTasks;
    @FXML private VBox doneTasks;
    @FXML private Button addTodoCard;
    @FXML private Button addProgressCard;
    @FXML private Button addDoneCard;

    private TaskModel taskModel;
    private int userId;


    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the controller by getting the current user
     * logged into the dashboard and loading their associated tasks.
     */
    @FXML
    public void initialize() {
        User user = Session.getInstance().getUser();
        if (user == null) return;

        userId = user.getID();
        taskModel = new TaskModel();

        loadTasksFromDb();

        // Set up add card buttons
        addTodoCard.setOnAction(e -> showAddTaskDialog("TODO"));
        addProgressCard.setOnAction(e -> showAddTaskDialog("IN_PROGRESS"));
        addDoneCard.setOnAction(e -> showAddTaskDialog("DONE"));

        setupDragAndDrop(todoTasks, "TODO");
        setupDragAndDrop(inProgressTasks, "IN_PROGRESS");
        setupDragAndDrop(doneTasks, "DONE");
    }

    /**
     * Shows a dialog to input a new task description and adds it to the DB.
     *
     * @param status The column/status for the new task ("TODO", "IN_PROGRESS", "DONE")
     */
    private void showAddTaskDialog(String status) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Enter task details:");

        // Dialog buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Dialog content
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

        // Handle Add button
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
     * Loads all tasks associated with userID and
     * adds them to the card.
     */
    private void loadTasksFromDb() {
        List<Task> tasks = taskModel.getTasksForUser(userId);

        todoTasks.getChildren().clear();
        inProgressTasks.getChildren().clear();
        doneTasks.getChildren().clear();

        for (Task task : tasks) {
            Node taskNode = createTaskNode(task);
            switch (task.getStatus()) {
                case "TODO" -> todoTasks.getChildren().add(taskNode);
                case "IN_PROGRESS" -> inProgressTasks.getChildren().add(taskNode);
                case "DONE" -> doneTasks.getChildren().add(taskNode);
            }
        }
    }

    /**
     * Builds the checkBox and delete button.
     *
     * @param task Task model
     * @return Node representing the task in the list
     */
    private Node createTaskNode(Task task) {
        Font font = Font.font("System", 16); // added this because for some reason descText isn't system font

        VBox container;
        Text descText = new Text(task.getDescription());
        Label priorityLabel = new Label(new String[]{"Low Priority","Medium Priority","High Priority"}[Math.min(task.getPriority(),2)]);

        descText.setFont(font);
        descText.getStyleClass().add("theme-text");
        priorityLabel.getStyleClass().add("task-priority");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox topRow = new HBox(10, priorityLabel, spacer, createDeleteButton(task));
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        if (!task.getDueDate().isEmpty()) {
            Label dueLabel = new Label("Due: " + task.getDueDate());
            dueLabel.getStyleClass().add("task-due");

            container = new VBox(5, topRow, descText, dueLabel);
        } else {
            container = new VBox(5, topRow, descText);
        }

        descText.wrappingWidthProperty().bind(container.widthProperty().subtract(65)); // subtract some padding for spacing
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 5, 0, 5));
        container.getStyleClass().add("kanban-task");
        container.setUserData(task);

        container.setOnDragDetected(dragEvent -> {
            Dragboard db = container.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);

            dragEvent.consume();
        });

        return container;
    }

    private Button createDeleteButton(Task task) {
        Button deleteBtn = new Button();
        FontIcon icon = new FontIcon("fas-times");
        deleteBtn.setGraphic(icon);
        deleteBtn.getStyleClass().add("color-transparent");
        icon.getStyleClass().add("theme-text");

        deleteBtn.setOnAction(e -> {
            boolean deleted = taskModel.deleteTask(task.getId());
            if (deleted) loadTasksFromDb(); // refresh column after delete
        });
        return deleteBtn;
    }

    /**
     * Configures drag-over and drag-dropped behavior for a task column.
     *
     * @param column VBox representing the column
     * @param status Status to assign tasks dropped here
     */
    private void setupDragAndDrop(VBox column, String status) {
        column.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() != column && dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
            dragEvent.consume();
        });

        column.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());

                taskModel.updateTaskStatus(taskId, status);
                loadTasksFromDb();

                success = true;
            }

            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });


        column.setOnDragEntered(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                column.setStyle("-fx-background-color: rgba(100,200,255,0.3);");
            }
        });

        column.setOnDragExited(event -> {
            column.setStyle(""); // reset style
        });
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
