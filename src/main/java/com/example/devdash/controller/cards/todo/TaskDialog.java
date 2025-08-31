package com.example.devdash.controller.cards.todo;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.todo.Sprint;
import com.example.devdash.model.todo.Task;
import com.example.devdash.model.todo.TaskModel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class TaskDialog {

    private static final TaskModel taskModel = new TaskModel();

    public static void showTaskDialog(Task task, Sprint selectedSprint, Runnable refreshAction) {
        boolean isEdit = task != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Task" : "Add Task");

        ButtonType saveButton = new ButtonType(isEdit ? "Save" : "Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField descField = new TextField(isEdit ? task.getDescription() : "");
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.getSelectionModel().select(isEdit ? task.getPriority() : 0);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("BACKLOG", "TODO", "IN_PROGRESS", "DONE");
        statusBox.getSelectionModel().select(isEdit ? task.getStatus() : "BACKLOG");

        DatePicker dueDatePicker = new DatePicker();
        if(isEdit && task.getDueDate() != null && !task.getDueDate().isBlank())
            dueDatePicker.setValue(LocalDate.parse(task.getDueDate()));

        VBox content = new VBox(10,
                new Label("Description:"), descField,
                new Label("Priority:"), priorityBox,
                new Label("Status:"), statusBox,
                new Label("Due Date:"), dueDatePicker);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if(btn == saveButton) {
                String desc = descField.getText().trim();
                int priority = priorityBox.getSelectionModel().getSelectedIndex();
                String status = statusBox.getSelectionModel().getSelectedItem();
                String dueDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : null;

                if(!desc.isBlank()) {
                    if(isEdit) {
                        taskModel.updateTask(task.getId(), desc, status, priority, dueDate);
                        taskModel.updateTaskTimestamp(task.getId());
                        if(selectedSprint != null && selectedSprint.getId() != 0 && !status.equals("BACKLOG"))
                            taskModel.assignTaskToSprint(task.getId(), selectedSprint.getId());
                        else taskModel.removeTaskFromSprint(task.getId());
                    } else {
                        int userId = Session.getInstance().getUser().getID();
                        taskModel.addTask(userId, desc, status, priority, dueDate);
                    }
                    refreshAction.run();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
