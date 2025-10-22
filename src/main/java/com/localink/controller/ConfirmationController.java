package com.localink.controller;

import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class ConfirmationController {
    @FXML private Label confirmationTitle;
    @FXML private Label bookingIdLabel;
    @FXML private Label serviceLabel;
    @FXML private Label providerLabel;
    @FXML private Label scheduleLabel;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm z");

    @FXML
    public void initialize() {
        Long bookingId = Session.getLastBookingId();
        Service svc = Session.getSelectedService();
        confirmationTitle.setText("Booking Confirmed!");
        bookingIdLabel.setText("Booking ID: " + (bookingId != null ? bookingId : "-"));
        if (svc != null) {
            serviceLabel.setText("Service: " + svc.getTitle());
            providerLabel.setText("Provider: " + svc.getProviderName());
        }
        if (Session.getLastScheduledAt() != null) {
            scheduleLabel.setText("Scheduled at: " + FMT.format(Session.getLastScheduledAt()));
        }
    }

    @FXML
    private void onGoHome() {
        ViewNavigator.navigate("/fxml/categories.fxml", "Localink - Browse Categories");
    }
}
