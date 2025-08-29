package com.example.devdash;

import com.example.devdash.helper.ui.FXMLUtils;
import com.example.devdash.helper.ui.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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

        FXMLUtils loaded = FXMLUtils.loadFXML("main/LoginPage");
        scene = new Scene(loaded.getRoot(), 1200, 800);
        stage.setTitle("DevDash");
        stage.setMinWidth(1200);
        stage.setMinHeight(800);

        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/css/style.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/css/light-theme.css")).toExternalForm());
        Font.loadFont(Objects.requireNonNull(Main.class.getResource("/fonts/Poppins-Light.ttf")).toExternalForm(), 12);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the root of the primary scene to a new FXML view
     *
     * @param fxml The name of the FXML file to load
     */
    public static void setRoot(String fxml) {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxml);
        scene.setRoot(loaded.getRoot());
    }

    /**
     * Switches the current scene's theme to the specified theme.
     *
     * @param theme The theme to apply to the current scene
     */
    public static void changeTheme(Theme theme) {
        scene.getStylesheets().removeIf(s -> s.contains("light-theme.css") || s.contains("dark-theme.css"));
        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("/css/" + theme.getCssFile())).toExternalForm()
        );
    }

    /**
     * Gets the current primary scene.
     *`
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