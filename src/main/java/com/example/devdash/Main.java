package com.example.devdash;

import com.example.devdash.helper.FXMLUtils;
import com.example.devdash.helper.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Initialises and displays the initial login window.
 *
 * @author Alexander Sukhin
 * @version 04/08/2025
 */
public class Main extends Application {
    private static Scene scene;

    /**
     * This method is called when the application is launched.
     * It sets up the primary stage (window) and loads the login page.
     *
     * @param stage The primary window
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLUtils loaded = FXMLUtils.loadFXML("LoginPage");
        scene = new Scene(loaded.getRoot(), 640, 480);
        stage.setTitle("DevDash");
        stage.setMinWidth(640);
        stage.setMinHeight(500);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("fxml/style.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("fxml/light-theme.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the root of the primary scene to a new FXML viewa
     *
     * @param fxml The name of the FXML file to load
     * @throws IOException If the new FXML file cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxml);
        scene.setRoot(loaded.getRoot());
    }

    public static void changeTheme(Theme theme) {
        scene.getStylesheets().removeIf(s -> s.contains("light-theme.css") || s.contains("dark-theme.css"));

        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("fxml/" + theme.getCssFile())).toExternalForm()
        );
    }


    /**
     * Gets the current primary scene.
     *
     * @return The current JavaFX Scene object
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Main method which launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}