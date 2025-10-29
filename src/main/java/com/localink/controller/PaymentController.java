package com.localink.controller;

import com.localink.dao.BookingDao;
import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class PaymentController {
    @FXML private Label serviceTitle;
    @FXML private Label amountLabel;
    @FXML private Label scheduleLabel;
    @FXML private TextField cardNumber;
    @FXML private TextField expiry;
    @FXML private PasswordField cvv;
    @FXML private Label errorLabel;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm z");

    @FXML
    public void initialize() {
        Service svc = Session.getSelectedService();
        if (svc != null) {
            serviceTitle.setText(svc.getTitle());
            amountLabel.setText(String.format("Amount: $%.2f", svc.getHourlyRate()));
        }
        if (Session.getLastScheduledAt() != null) {
            scheduleLabel.setText("Scheduled at: " + FMT.format(Session.getLastScheduledAt()));
        }
    }

    @FXML
    private void onBack() {
        ViewNavigator.navigate("/fxml/booking.fxml", "Localink - Booking");
    }

    @FXML
    private void onPay() {
        // Minimal validation
        if (cardNumber.getText().isBlank() || expiry.getText().isBlank() || cvv.getText().isBlank()) {
            errorLabel.setText("Enter payment details");
            return;
        }
        try {
            Service svc = Session.getSelectedService();
            long bookingId = new BookingDao().create(Session.getCurrentUserId(), svc.getId(), Session.getLastScheduledAt());
            Session.setLastBookingId(bookingId);
            ViewNavigator.navigate("/fxml/confirmation.fxml", "Localink - Confirmation");
        } catch (Exception ex) {
            errorLabel.setText("Payment/Booking failed");
        }
    }
}
