package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Factory class for creating JavaFX nodes representing individual tasks.
 * Each task node displays the description, priority, optional due date,
 * and a delete button. Nodes are also configured for drag-and-drop between
 * task columns.
 *
 * Author: Alexander Sukhin
 * Version: 30/08/2025
 */
public class TaskNodeFactory {

    private final TaskModel taskModel;
    private final Runnable refreshAction;

    /**
     * Constructs a TaskNodeFactory with a TaskModel and a refresh callback.
     *
     * @param taskModel     the TaskModel for database operations
     * @param refreshAction Runnable to refresh the task UI
     */
    public TaskNodeFactory(TaskModel taskModel, Runnable refreshAction) {
        this.taskModel = taskModel;
        this.refreshAction = refreshAction;
    }

    /**
     * Creates a JavaFX Node representing a task.
     *
     * @param task Task model containing description, priority, due date, and ID
     * @return A JavaFX Node representing the task
     */
    public Node createTaskNode(Task task) {
        Font font = Font.font("System", 16);

        Text descText = new Text(task.getDescription());
        descText.setFont(font);
        descText.getStyleClass().add("theme-text");

        Label priorityLabel = new Label(new String[]{"Low","Medium","High"}[Math.min(task.getPriority(),2)]);
        priorityLabel.getStyleClass().add("task-priority");

        switch (task.getPriority()) {
            case 0 -> priorityLabel.setStyle("-fx-background-color: #27AE60");
            case 1 -> priorityLabel.setStyle("-fx-background-color: #F1C40F");
            case 2 -> priorityLabel.setStyle("-fx-background-color: #E74C3C");
        }

        priorityLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox topRow = new HBox(10, priorityLabel, spacer, createDeleteButton(task));
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox container;

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            Label dueLabelPrefix = new Label("Due: ");
            dueLabelPrefix.getStyleClass().add("task-due");

            Label dueLabel = new Label(task.getDueDate());
            LocalDate dueDate = LocalDate.parse(task.getDueDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();

            if (dueDate.isBefore(today)) {
                // create warning triangle icon
                FontIcon icon = new FontIcon("fas-exclamation-triangle");
                icon.getStyleClass().add("theme-text");
                Tooltip.install(icon, new Tooltip("Overdue"));

                Label space = new Label(" ");

                HBox dueHBox = new HBox(dueLabelPrefix, dueLabel, space, icon);
                dueHBox.setAlignment(Pos.CENTER_LEFT);

                container = new VBox(5, topRow, descText, dueHBox);
            } else {
                HBox dueHBox = new HBox(dueLabelPrefix, dueLabel);
                dueHBox.setAlignment(Pos.CENTER_LEFT);
                container = new VBox(5, topRow, descText, dueHBox);
            }

        } else {
            container = new VBox(5, topRow, descText);
        }

        descText.wrappingWidthProperty().bind(container.widthProperty().subtract(65));
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 5, 0, 5));
        container.getStyleClass().add("kanban-task");
        container.setUserData(task);

        // drag setup
        container.setOnDragDetected(event -> {
            Dragboard db = container.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            container.getStyleClass().add("dragging");
            event.consume();
        });

        container.setOnDragDone(event -> {
            container.getStyleClass().remove("dragging");
            event.consume();
        });

        return container;
    }

    /**
     * Creates a delete button for a task.
     *
     * @param task Task model to delete
     * @return A JavaFX Button configured to delete the task
     */
    private Button createDeleteButton(Task task) {
        Button deleteBtn = new Button();
        FontIcon icon = new FontIcon("fas-times");
        deleteBtn.setGraphic(icon);
        deleteBtn.getStyleClass().add("color-transparent");
        icon.getStyleClass().add("theme-text");

        deleteBtn.setOnAction(e -> {
            boolean deleted = taskModel.deleteTask(task.getId());
            if (deleted) refreshAction.run();
        });
        return deleteBtn;
    }
}
