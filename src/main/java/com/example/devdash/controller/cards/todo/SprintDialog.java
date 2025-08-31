package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.Sprint;
import com.example.devdash.model.todo.SprintModel;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class SprintDialog {

    public static void showAddSprintDialog(int userId, SprintModel sprintModel, Runnable onSuccess) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Sprint");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Sprint name");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        VBox content = new VBox(10, new Label("Name:"), nameField,
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == addButtonType) {
                String name = nameField.getText().trim();
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();

                if(!name.isBlank() && start != null && end != null && !end.isBefore(start)) {
                    sprintModel.addSprint(userId, name, start, end);
                    onSuccess.run();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Sprint");
                    alert.setHeaderText("Enter valid sprint details.");
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
