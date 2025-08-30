package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.TaskModel;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

/**
 * Handles drag-and-drop behavior for task columns in the To-do Card.
 *
 * Author: Alexander Sukhin
 * Version: 30/08/2025
 */
public class DragAndDropHandler {

    private final TaskModel taskModel;
    private final Runnable refreshAction;


    /**
     * Constructs a DragAndDropHandler with a TaskModel and a refresh callback.
     *
     * @param taskModel     TaskModel used for updating task status
     * @param refreshAction Runnable to refresh the UI after task changes
     */
    public DragAndDropHandler(TaskModel taskModel, Runnable refreshAction) {
        this.taskModel = taskModel;
        this.refreshAction = refreshAction;
    }


    /**
     * Sets up drag-and-drop behavior for a task column.
     *
     * @param column VBox representing the task column
     * @param status Status to assign to tasks dropped in this column
     */
    public void setupDragAndDrop(VBox column, String status) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());
                taskModel.updateTaskStatus(taskId, status);
                refreshAction.run();
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        column.setOnDragEntered(event -> {
            if (event.getGestureSource() instanceof Node sourceNode
                    && sourceNode.getParent() != column
                    && event.getDragboard().hasString()) {
                column.setStyle("-fx-border-color: dodgerblue; -fx-border-width: 2; -fx-border-radius: 6;");
            }
        });

        column.setOnDragExited(event -> {
            column.setStyle("");
        });
    }
}