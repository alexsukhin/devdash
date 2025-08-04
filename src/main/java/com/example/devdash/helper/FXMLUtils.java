package com.example.devdash.helper;

import com.example.devdash.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class FXMLUtils {
    private final Parent root;
    private final Object controller;

    private FXMLUtils(Parent root, Object controller) {
        this.root = root;
        this.controller = controller;
    }

    public static FXMLUtils loadFXML(String fxml) {
        try {
            URL url = Main.class.getResource("fxml/" + fxml + ".fxml");

            if (url == null) {
                System.err.println("FXML resource not found: " + fxml);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Object controller = loader.getController();
            return new FXMLUtils(root, controller);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxml + " - " + e.getMessage());
            return null;
        }
    }

    public Parent getRoot() {
        return root;
    }

    public Object getController() {
        return controller;
    }
}
