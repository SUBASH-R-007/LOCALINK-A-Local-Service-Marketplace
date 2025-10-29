package com.localink.controller;

import com.localink.dao.BookingDao;
import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BookingController {
    @FXML private Label serviceTitle;
    @FXML private Label serviceProvider;
    @FXML private Label serviceRate;
    @FXML private TextArea notesField; // placeholder for future use
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> hourCombo;
    @FXML private ComboBox<Integer> minuteCombo;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        Service svc = Session.getSelectedService();
        if (svc == null) {
            errorLabel.setText("No service selected");
            return;
        }
        serviceTitle.setText(svc.getTitle());
        serviceProvider.setText("Provider: " + svc.getProviderName());
        serviceRate.setText(String.format("Rate: $%.2f/h", svc.getHourlyRate()));

        datePicker.setValue(LocalDate.now().plusDays(1));
        hourCombo.setItems(FXCollections.observableArrayList(range(8, 20))); // 8..19
        minuteCombo.setItems(FXCollections.observableArrayList(0, 15, 30, 45));
        hourCombo.getSelectionModel().select(Integer.valueOf(10));
        minuteCombo.getSelectionModel().select(Integer.valueOf(0));
    }

    private static java.util.List<Integer> range(int startIncl, int endExcl) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int i = startIncl; i < endExcl; i++) list.add(i);
        return list;
    }

    @FXML
    private void onBack() {
        ViewNavigator.navigate("/fxml/services_list.fxml", "Localink - Services");
    }

    @FXML
    private void onConfirm() {
        Service svc = Session.getSelectedService();
        if (svc == null) return;
        LocalDate d = datePicker.getValue();
        Integer h = hourCombo.getValue();
        Integer m = minuteCombo.getValue();
        if (d == null || h == null || m == null) {
            errorLabel.setText("Please select date and time");
            return;
        }
        LocalTime t = LocalTime.of(h, m);
        ZonedDateTime when = ZonedDateTime.of(d, t, ZoneId.systemDefault());
        Session.setLastScheduledAt(when);
        ViewNavigator.navigate("/fxml/payment.fxml", "Localink - Payment");
    }
}

