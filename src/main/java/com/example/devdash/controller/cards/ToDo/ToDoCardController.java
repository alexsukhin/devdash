package com.example.devdash.controller.cards.ToDo;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.Session;
import com.example.devdash.model.Task;
import com.example.devdash.model.TaskModel;
import com.example.devdash.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the To-do card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class ToDoCardController implements DashboardCard {

    @FXML private Node rootNode;
    @FXML private TextField addTask;
    @FXML private VBox tasksContainer;

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

        if (user != null) {
            userId = user.getID();
            taskModel = new TaskModel();
            loadTasksFromDb();
        } else {
            System.err.println("User not logged in; ToDo card won't load tasks.");
        }
    }


    /**
     * Called when the user submits a new task (presses Enter in the text field).
     */
    @FXML
    public void onEnter() {
        String text = addTask.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        boolean success = taskModel.addTask(userId, text);
        if (success) {
            loadTasksFromDb();
            addTask.clear();
            addTask.getParent().requestFocus();
        } else {
            System.err.println("Failed to add task");
        }
    }

    /**
     * Loads all tasks associated with userID and
     * adds them to the card.
     */
    private void loadTasksFromDb() {
        List<Task> tasks = taskModel.getTasksForUser(userId);
        tasksContainer.getChildren().clear();
        for (Task task : tasks) {
            Node taskNode = createTaskNode(task);
            tasksContainer.getChildren().add(taskNode);
        }
    }

    /**
     * Builds the checkBox and delete button.
     *
     * @param task Task model
     * @return Node representing the task in the list
     */
    private Node createTaskNode(Task task) {
        CheckBox checkBox = new CheckBox(task.getDescription());
        checkBox.setSelected(task.isCompleted());

        // Strike-through when completed
        checkBox.selectedProperty().addListener((obs, was, isNow) -> {
            task.setCompleted(isNow);
            taskModel.updateTaskCompletion(task.getId(), isNow);
            if (isNow) {
                checkBox.setStyle("-fx-strikethrough: true;");
            } else {
                checkBox.setStyle("-fx-strikethrough: false;");
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button deleteBtn = new Button();
        deleteBtn.setStyle("-fx-background-color: rgba(0,0,0,0);");
        FontIcon icon = new FontIcon("fa-close");


        HBox container = new HBox(10, checkBox, spacer, deleteBtn);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.setUserData(task);

        deleteBtn.setGraphic(icon);
        deleteBtn.setOnAction(e -> {
            boolean deleted = taskModel.deleteTask(task.getId());
            if (deleted) {
                tasksContainer.getChildren().removeIf(node -> node.getUserData() == task);
            } else {
                System.err.println("Failed to delete task");
            }
        });


        return container;
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
