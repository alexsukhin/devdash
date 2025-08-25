package com.example.devdash.helper.ui;

import com.example.devdash.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for loading FXML files and their controllers.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class FXMLUtils {

    // The root node loaded from the FXML file
    private final Parent root;

    // The controller associated with the loaded FXML file
    private final Object controller;

    /**
     * Constructor to create an instance holding both root and controller.
     *
     * @param root The root node of the loaded FXML
     * @param controller The controller associated with the FXML
     */
    private FXMLUtils(Parent root, Object controller) {
        this.root = root;
        this.controller = controller;
    }

    /**
     * Loads an FXML file.
     *
     * @param fxml The name of the FXML file.
     * @return An FXMLUtils instance with root and controller or null if loading fails
     */
    public static FXMLUtils loadFXML(String fxml) {
        try {
            URL url = Main.class.getResource("fxml/" + fxml + ".fxml");

            if (url == null) {
                System.err.println("FXML resource not found: " + fxml);
                return null;
            }

            // Load FXML and get controller
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Object controller = loader.getController();

            return new FXMLUtils(root, controller);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxml + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the root node loaded from the FXML.
     *
     * @return The root node
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Gets the controller associated with the loaded FXML.
     *
     * @return The controller object
     */
    public Object getController() {
        return controller;
    }
}
