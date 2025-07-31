package com.example.devdash.controller.cards;

import javafx.scene.Node;

public interface DashboardCard {
    Node getView();
    void refresh();
}
