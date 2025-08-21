module com.example.devdash {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.naming;
    requires org.kordamp.ikonli.javafx;
    requires org.kohsuke.github.api;


    opens com.example.devdash to javafx.fxml;
    exports com.example.devdash;
    exports com.example.devdash.controller;
    opens com.example.devdash.controller to javafx.fxml;
    exports com.example.devdash.controller.cards;
    opens com.example.devdash.controller.cards to javafx.fxml;
    exports com.example.devdash.controller.cards.Pomodoro;
    opens com.example.devdash.controller.cards.Pomodoro to javafx.fxml;
    exports com.example.devdash.controller.cards.ToDo;
    opens com.example.devdash.controller.cards.ToDo to javafx.fxml;
    exports com.example.devdash.controller.cards.GitHub;
    opens com.example.devdash.controller.cards.GitHub to javafx.fxml;
    exports com.example.devdash.controller.cards.TypingTest;
    opens com.example.devdash.controller.cards.TypingTest to javafx.fxml;
    exports com.example.devdash.model;
    opens com.example.devdash.model to javafx.fxml;
    exports com.example.devdash.helper;
    opens com.example.devdash.helper to javafx.fxml;
}