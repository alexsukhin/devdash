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
     * Constructs a TaskNodeFactory with the required dependencies.
     *
     * @param taskModel        TaskModel used for database operations
     * @param refreshAction    Runnable callback to refresh the task board UI
     * @param editTaskCallback Callback function invoked when user edits a task
     */
    public TaskNodeFactory(TaskModel taskModel, Runnable refreshAction, Consumer<Task> editTaskCallback) {
        this.taskModel = taskModel;
        this.refreshAction = refreshAction;
        this.editTaskCallback = editTaskCallback;
    }

    /**
     * Creates a JavaFX Node representing a task.
     * This includes description, priority, due date, update time,
     * delete/edit menu, and drag support.
     *
     * @param task Task object containing description, priority, due date, and status
     * @return VBox node representing the task
     */
    public Node createTaskNode(Task task) {
        Text descText = createDescription(task);
        Label priorityLabel = createPriorityLabel(task);
        HBox topRow = createTopRow(task, priorityLabel);
        Label updatedLabel = createUpdatedLabel(task);
        VBox container = createContainer(task, descText, topRow, updatedLabel);
        setupDrag(container, task);
        return container;
    }

    /**
     * Builds the text node for the task description.
     *
     * @param task The task whose description is displayed
     * @return Text node styled with theme
     */
    private Text createDescription(Task task) {
        Text text = new Text(task.getDescription());
        text.setFont(Font.font("System", 16));
        text.getStyleClass().add("theme-text");
        return text;
    }

    /**
     * Creates a label showing the task priority level with colored background.
     *
     * @param task The task whose priority is displayed
     * @return Label with styled priority indicator
     */
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

    /**
     * Creates the top row of a task node, containing:
     * Priority label, Spacer, Task menu button
     *
     * @param task          Task being represented
     * @param priorityLabel Already constructed priority label
     * @return HBox containing top row elements
     */
    private HBox createTopRow(Task task, Label priorityLabel) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox row = new HBox(10, priorityLabel, spacer, createTaskMenuButton(task));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Creates a label for the "last updated" timestamp of the task.
     *
     * @param task The task being represented
     * @return Label with timestamp or null if no date is set
     */
    private Label createUpdatedLabel(Task task) {
        if (task.getFormattedUpdatedAt().isEmpty()) return null;
        Label label = new Label(task.getFormattedUpdatedAt());
        label.setStyle("-fx-font-size: 13px;");
        label.getStyleClass().add("theme-text");
        return label;
    }

    /**
     * Builds the container VBox holding all task UI elements.
     * Dynamically includes due date row and updated timestamp if available.
     *
     * @param task         Task being represented
     * @param descText     Description text node
     * @param topRow       Top row
         * @param updatedLabel Updated timestamp label
     * @return Configured VBox container
     */
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

    /**
     * Creates a row displaying the task's due date.
     * If overdue, adds a warning icon with tooltip.
     *
     * @param task Task being represented
     * @return HBox row showing due date
     */
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

    /**
     * Creates a button with a context menu containing:
     * Edit option → triggers editTaskCallback
     * Delete option → deletes task and refreshes UI
     *
     * @param task Task being represented
     * @return Configured button with context menu
     */
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

    /**
     * Creates a delete button that removes the task from DB
     * and refreshes the UI when pressed.
     *
     * @param task Task to delete
     * @return Button with delete icon
     */
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

    /**
     * Configures drag-and-drop support for the task container.
     * - On drag start: places task ID into drag board
     * - On drag done: removes "dragging" style
     *
     * @param container VBox container representing the task
     * @param task      Task being dragged
     */
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
