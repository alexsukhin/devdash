package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.util.function.Consumer;

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
    private final Consumer<Task> editTaskCallback;

    /**
     * Constructs a TaskNodeFactory with a TaskModel and a refresh callback.
     *
     * @param taskModel     the TaskModel for database operations
     * @param refreshAction Runnable to refresh the task UI
     */
    public TaskNodeFactory(TaskModel taskModel, Runnable refreshAction, Consumer<Task> editTaskCallback) {
        this.taskModel = taskModel;
        this.refreshAction = refreshAction;
        this.editTaskCallback = editTaskCallback;
    }

    /**
     * Creates a JavaFX Node representing a task.
     *
     * @param task Task model containing description, priority, due date, and ID
     * @return A JavaFX Node representing the task
     */
    /** Main method to create a fully-featured task node */
    public Node createTaskNode(Task task) {
        Text descText = createDescription(task);
        Label priorityLabel = createPriorityLabel(task);
        HBox topRow = createTopRow(task, priorityLabel);
        Label updatedLabel = createUpdatedLabel(task);
        VBox container = createContainer(task, descText, topRow, updatedLabel);
        setupDrag(container, task);
        return container;
    }

    private Text createDescription(Task task) {
        Text text = new Text(task.getDescription());
        text.setFont(Font.font("System", 16));
        text.getStyleClass().add("theme-text");
        return text;
    }

    private Label createPriorityLabel(Task task) {
        Label label = new Label(new String[]{"Low","Medium","High"}[Math.min(task.getPriority(), 2)]);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("task-priority");

        switch (task.getPriority()) {
            case 0 -> label.setStyle("-fx-background-color: #27AE60");
            case 1 -> label.setStyle("-fx-background-color: #F1C40F");
            case 2 -> label.setStyle("-fx-background-color: #E74C3C");
        }
        return label;
    }

    private HBox createTopRow(Task task, Label priorityLabel) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox row = new HBox(10, priorityLabel, spacer, createTaskMenuButton(task));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label createUpdatedLabel(Task task) {
        if (task.getFormattedUpdatedAt().isEmpty()) return null;
        Label label = new Label(task.getFormattedUpdatedAt());
        label.setStyle("-fx-font-size: 13px;");
        label.getStyleClass().add("theme-text");
        return label;
    }

    private VBox createContainer(Task task, Text descText, HBox topRow, Label updatedLabel) {
        VBox container;

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            HBox dueRow = createDueDateRow(task);
            if (updatedLabel != null) container = new VBox(5, topRow, descText, dueRow, updatedLabel);
            else container = new VBox(5, topRow, descText, dueRow);
        } else {
            if (updatedLabel != null) container = new VBox(5, topRow, descText, updatedLabel);
            else container = new VBox(5, topRow, descText);
        }

        descText.wrappingWidthProperty().bind(container.widthProperty().subtract(65));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 5, 0, 5));
        container.getStyleClass().add("kanban-task");
        container.setUserData(task);

        return container;
    }

    private HBox createDueDateRow(Task task) {
        Label duePrefix = new Label("Due: ");
        duePrefix.getStyleClass().add("task-due");
        Label dueLabel = new Label(task.getDueDate());

        LocalDate dueDate = LocalDate.parse(task.getDueDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate today = LocalDate.now();
        HBox dueRow;

        if (dueDate.isBefore(today)) {
            FontIcon warningIcon = new FontIcon("fas-exclamation-triangle");
            warningIcon.getStyleClass().add("theme-text");
            Tooltip.install(warningIcon, new Tooltip("Overdue"));
            dueRow = new HBox(duePrefix, dueLabel, new Label(" "), warningIcon);
        } else {
            dueRow = new HBox(duePrefix, dueLabel);
        }
        dueRow.setAlignment(Pos.CENTER_LEFT);
        return dueRow;
    }

    private Button createTaskMenuButton(Task task) {
        Button menuBtn = new Button();
        menuBtn.getStyleClass().add("color-transparent");
        FontIcon icon = new FontIcon("fas-ellipsis-v");
        icon.getStyleClass().add("theme-text");
        menuBtn.setGraphic(icon);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> editTaskCallback.accept(task));

        CustomMenuItem deleteItem = new CustomMenuItem(createDeleteButton(task));
        deleteItem.setHideOnClick(false);

        contextMenu.getItems().addAll(editItem, deleteItem);

        menuBtn.setOnAction(e -> contextMenu.show(menuBtn, javafx.geometry.Side.BOTTOM, 0, 0));
        return menuBtn;
    }

    private Button createDeleteButton(Task task) {
        Button deleteBtn = new Button();
        FontIcon icon = new FontIcon("fas-times");
        deleteBtn.setGraphic(icon);
        deleteBtn.getStyleClass().add("color-transparent");
        icon.getStyleClass().add("theme-text");
        deleteBtn.setOnAction(e -> {
            if (taskModel.deleteTask(task.getId())) refreshAction.run();
        });
        return deleteBtn;
    }

    private void setupDrag(VBox container, Task task) {
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
    }

}
