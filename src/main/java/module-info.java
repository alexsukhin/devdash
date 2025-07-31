module com.example.devdash {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.naming;


    opens com.example.devdash to javafx.fxml;
    exports com.example.devdash;
    exports com.example.devdash.controller;
    opens com.example.devdash.controller to javafx.fxml;
    exports com.example.devdash.controller.cards;
    opens com.example.devdash.controller.cards to javafx.fxml;
}