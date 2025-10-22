package com.localink.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CustomerDashboardController {
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<String> resultsList;

    @FXML
    public void initialize() {
        resultsList.getItems().addAll(
                "Electrician - John Doe",
                "Tutor - Jane Smith",
                "Cleaner - Alex Johnson"
        );
    }
}
