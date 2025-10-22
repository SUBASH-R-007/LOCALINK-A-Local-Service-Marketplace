package com.localink.controller;

import com.localink.util.ViewNavigator;
import com.localink.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Label errorLabel;
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        roleChoice.getItems().addAll("CUSTOMER", "PROVIDER");
        roleChoice.setValue("CUSTOMER");
    }

    @FXML
    private void onRegister(ActionEvent e) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleChoice.getValue();
        try {
            authService.register(name, email, password, role);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful. Please login.", ButtonType.OK);
            alert.showAndWait();
            ViewNavigator.navigate("/fxml/login.fxml", "Localink - Login");
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Registration failed");
        }
    }

    @FXML
    private void onBackToLogin(ActionEvent e) {
        ViewNavigator.navigate("/fxml/login.fxml", "Localink - Login");
    }
}
