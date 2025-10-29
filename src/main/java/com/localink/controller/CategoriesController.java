package com.localink.controller;

import com.localink.dao.ServiceDao;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class CategoriesController {
    @FXML private ListView<String> categoryList;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        try {
            List<String> categories = new ServiceDao().listCategories();
            categoryList.setItems(FXCollections.observableArrayList(categories));
            categoryList.setOnMouseClicked(e -> {
                String cat = categoryList.getSelectionModel().getSelectedItem();
                if (cat != null) {
                    Session.setSelectedCategory(cat);
                    ViewNavigator.navigate("/fxml/services_list.fxml", "Localink - " + cat);
                }
            });
        } catch (SQLException ex) {
            errorLabel.setText("Failed to load categories");
        }
    }

    @FXML
    private void onLogout() {
        Session.setCurrentUserId(null);
        Session.setCurrentUserRole(null);
        Session.setCurrentUserName(null);
        ViewNavigator.navigate("/fxml/login.fxml", "Localink - Login");
    }
}

