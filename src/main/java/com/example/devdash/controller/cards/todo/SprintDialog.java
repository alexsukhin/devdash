package com.example.devdash.controller.cards.todo;

import com.example.devdash.model.todo.Sprint;
import com.example.devdash.model.todo.SprintModel;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 * Utility class for displaying a dialog to create new Sprint objects.
 * If the input is valid, the sprint is added to the database via SprintModel.
 *
 * Author: Alexander Sukhin
 * Version: 30/08/2025
 */
public class SprintDialog {

    /**
     * Displays a dialog for creating and adding a new {@link Sprint}.
     *
     * @param userId    The user's ID
     * @param sprintModel The SprintModel instance used to update the database
     * @param onSuccess A callback that will be executed if the sprint is successfully created.
     */
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

        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            LocalDate today = LocalDate.now();

            if (!name.isBlank() && start != null && end != null
                    && !end.isBefore(start) && !start.isBefore(today) && !end.isBefore(today)) {
                sprintModel.addSprint(userId, name, start, end);
                onSuccess.run();
            } else {
                event.consume(); // ❌ prevent dialog from closing
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Sprint");
                alert.setHeaderText("Enter valid sprint details.");
                alert.setContentText("Name must not be empty. Start and end dates must be today or later, and the end date cannot be before the start date.");
                alert.showAndWait();
            }
        });

        dialog.showAndWait();
    }
}
