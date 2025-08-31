package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.TaskModel;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.util.function.Supplier;

public class DragAndDropHandler {

    private final TaskModel taskModel;
    private final Runnable refreshAction;
    private final Supplier<Integer> sprintIdSupplier;

    public DragAndDropHandler(TaskModel taskModel, Runnable refreshAction, Supplier<Integer> sprintIdSupplier) {
        this.taskModel = taskModel;
        this.refreshAction = refreshAction;
        this.sprintIdSupplier = sprintIdSupplier;
    }

    public void setupDragAndDrop(VBox column, String status) {
        column.setOnDragOver(event -> {
            if(event.getGestureSource() != column && event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());
                taskModel.updateTaskStatus(taskId, status);
                taskModel.updateTaskTimestamp(taskId);

                int sprintId = sprintIdSupplier.get();
                if(status.equals("BACKLOG")) taskModel.removeTaskFromSprint(taskId);
                else if(sprintId != 0) taskModel.assignTaskToSprint(taskId, sprintId);

                refreshAction.run();
                event.setDropCompleted(true);
            } else event.setDropCompleted(false);

            event.consume();
        });

        column.setOnDragEntered(event -> {
            if (event.getGestureSource() instanceof Node sourceNode
                    && sourceNode.getParent() != column
                    && event.getDragboard().hasString()) {
                column.setStyle("-fx-border-color: dodgerblue; -fx-border-width: 2; -fx-border-radius: 6;");
            }
        });

        column.setOnDragExited(event -> column.setStyle(""));
    }
}
