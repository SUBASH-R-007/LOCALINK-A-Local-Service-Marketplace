package com.localink.controller;

import com.localink.util.ViewNavigator;
import com.localink.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.localink.dao.ServiceDao;
import com.localink.model.Service;
import javafx.scene.layout.VBox;
import com.localink.dao.BookingDao;
import com.localink.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class ProviderDashboardController {
    @FXML private ListView<String> servicesList;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Long> colId;
    @FXML private TableColumn<Booking, Long> colCustomer;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, Object> colWhen;

    private final ObservableList<Booking> bookingItems = FXCollections.observableArrayList();
    private Timeline autoRefresh;

    @FXML
    public void initialize() {
        loadServices();
        servicesList.setPlaceholder(new Label("No services"));
        bookingsTable.setPlaceholder(new Label("No bookings"));

        // Setup bookings table columns
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colCustomer != null) colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colWhen != null) colWhen.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        bookingsTable.setItems(bookingItems);

        loadBookings();

        // Periodically refresh bookings so new ones appear without reopening the view
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadBookings()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    @FXML private void onAddService() {
        if (Session.getCurrentUserId() == null) {
            new Alert(Alert.AlertType.ERROR, "Not logged in", ButtonType.OK).showAndWait();
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Service");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        TextField rateField = new TextField();
        rateField.setPromptText("Hourly rate (e.g. 25.0)");
        VBox box = new VBox(8, new Label("Title"), titleField,
                new Label("Category"), categoryField,
                new Label("Description"), descArea,
                new Label("Hourly Rate"), rateField);
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String title = titleField.getText();
                String category = categoryField.getText();
                String desc = descArea.getText();
                String rateStr = rateField.getText();
                if (title == null || title.isBlank() || category == null || category.isBlank() || rateStr == null || rateStr.isBlank()) {
                    new Alert(Alert.AlertType.WARNING, "Title, Category and Rate are required", ButtonType.OK).showAndWait();
                    return;
                }
                double rate;
                try { rate = Double.parseDouble(rateStr); } catch (NumberFormatException nfe) {
                    new Alert(Alert.AlertType.WARNING, "Rate must be a number", ButtonType.OK).showAndWait();
                    return;
                }
                try {
                    new ServiceDao().insert(Session.getCurrentUserId(), title.trim(), category.trim(), desc == null ? "" : desc.trim(), rate);
                    loadServices();
                    new Alert(Alert.AlertType.INFORMATION, "Service added", ButtonType.OK).showAndWait();
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR, "Failed to add service", ButtonType.OK).showAndWait();
                }
            }
        });
    }
    @FXML private void onEditService() {
        String sel = servicesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a service to edit", ButtonType.OK).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Edit Service: " + sel, ButtonType.OK).showAndWait();
    }
    @FXML private void onToggleActive() {
        String sel = servicesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a service to toggle", ButtonType.OK).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Toggled active for: " + sel, ButtonType.OK).showAndWait();
    }
    @FXML private void onAccept() {
        Object sel = bookingsTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a booking to accept", ButtonType.OK).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Accepted booking", ButtonType.OK).showAndWait();
    }
    @FXML private void onReject() {
        Object sel = bookingsTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a booking to reject", ButtonType.OK).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Rejected booking", ButtonType.OK).showAndWait();
    }
    @FXML private void onComplete() {
        Object sel = bookingsTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a booking to complete", ButtonType.OK).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Marked booking completed", ButtonType.OK).showAndWait();
    }

    @FXML
    private void onLogout() {
        Session.setCurrentUserId(null);
        Session.setCurrentUserRole(null);
        Session.setCurrentUserName(null);
        if (autoRefresh != null) autoRefresh.stop();
        ViewNavigator.navigate("/fxml/login.fxml", "Localink - Login");
    }

    private void loadServices() {
        servicesList.getItems().clear();
        Long providerId = Session.getCurrentUserId();
        if (providerId == null) return;
        try {
            List<Service> svcs = new ServiceDao().listByProvider(providerId);
            for (Service s : svcs) {
                servicesList.getItems().add(s.getTitle() + " — $" + s.getHourlyRate() + "/h");
            }
        } catch (SQLException ex) {
            // ignore UI-level errors for now
        }
    }

    private void loadBookings() {
        Long providerId = Session.getCurrentUserId();
        if (providerId == null) return;
        try {
            List<Booking> list = new BookingDao().listByProvider(providerId);
            bookingItems.setAll(list);
            bookingsTable.refresh();
        } catch (SQLException ex) {
            // silently ignore in UI
        }
    }
}
