package com.example.devdash.helper;

import com.example.devdash.Main;
import com.example.devdash.controller.cards.DashboardCard;
import javafx.fxml.FXMLLoader;

import java.io.IOException;


public class DashboardCardFactory {
    public static DashboardCard loadCard(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Failed to load card: " + fxml + " " + e.getMessage());
            return null;
        }
    }
}
