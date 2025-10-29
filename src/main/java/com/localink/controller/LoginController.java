package com.localink.controller;

import com.localink.util.ViewNavigator;
import com.localink.service.AuthService;
import com.localink.model.User;
import com.localink.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorLabel;
    private final AuthService authService = new AuthService();

    @FXML
    private void onLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            User user = authService.login(email, password);
            if (user == null) {
                errorLabel.setText("Invalid email or password");
                return;
            }
            Session.setCurrentUserId(user.getId());
            Session.setCurrentUserRole(user.getRole());
            Session.setCurrentUserName(user.getName());
            String role = user.getRole();
            if ("PROVIDER".equalsIgnoreCase(role)) {
                ViewNavigator.navigate("/fxml/provider_dashboard.fxml", "Localink - Provider Dashboard");
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                ViewNavigator.navigate("/fxml/admin_dashboard.fxml", "Localink - Admin Dashboard");
            } else {
                ViewNavigator.navigate("/fxml/categories.fxml", "Localink - Browse Categories");
            }
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Login failed");
        }
    }

    @FXML
    private void onGoToRegister(ActionEvent e) {
        ViewNavigator.navigate("/fxml/register.fxml", "Localink - Register");
    }
}

