package com.example.devdash;

import com.example.devdash.helper.FXMLUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML("LoginPage");
        scene = new Scene(loaded.getRoot(), 640, 480);
        stage.setTitle("DevDash");
        stage.setMinWidth(640);
        stage.setMinHeight(500);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("fxml/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxml);
        scene.setRoot(loaded.getRoot());
    }

    public static Scene getScene() {
        return scene;
    }


    public static void main(String[] args) {
        launch();
    }
}