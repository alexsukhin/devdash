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
    exports com.example.devdash.controller.cards.pomodoro;
    opens com.example.devdash.controller.cards.pomodoro to javafx.fxml;
    exports com.example.devdash.controller.cards.todo;
    opens com.example.devdash.controller.cards.todo to javafx.fxml;
    exports com.example.devdash.controller.cards.github;
    opens com.example.devdash.controller.cards.github to javafx.fxml;
    exports com.example.devdash.controller.cards.typingtest;
    opens com.example.devdash.controller.cards.typingtest to javafx.fxml;
    exports com.example.devdash.model.auth;
    opens com.example.devdash.model.auth to javafx.fxml;
    exports com.example.devdash.model.github;
    opens com.example.devdash.model.github to javafx.fxml;
    exports com.example.devdash.model.pomodoro;
    opens com.example.devdash.model.pomodoro to javafx.fxml;
    exports com.example.devdash.model.todo;
    opens com.example.devdash.model.todo to javafx.fxml;
    exports com.example.devdash.model.typingtest;
    opens com.example.devdash.model.typingtest to javafx.fxml;
    exports com.example.devdash.helper.ui;
    opens com.example.devdash.helper.ui to javafx.fxml;
    exports com.example.devdash.helper.data;
    opens com.example.devdash.helper.data to javafx.fxml;
}