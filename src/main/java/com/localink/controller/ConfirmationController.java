package com.localink.controller;

import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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

    @FXML
    private void onSaveReceipt() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Receipt");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        chooser.setInitialFileName("localink-receipt.pdf");
        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        Long bookingId = Session.getLastBookingId();
        Service svc = Session.getSelectedService();
        String user = Session.getCurrentUserName();
        String when = Session.getLastScheduledAt() != null ? FMT.format(Session.getLastScheduledAt()) : "-";
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = 750;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(50, y);
                cs.showText("Localink - Payment Receipt");
                cs.endText();

                y -= 30;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Booking ID: " + (bookingId != null ? bookingId : "-"));
                cs.endText();

                y -= 18;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Customer: " + (user != null ? user : "-"));
                cs.endText();

                if (svc != null) {
                    y -= 18;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Service: " + svc.getTitle());
                    cs.endText();

                    y -= 18;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Provider: " + svc.getProviderName());
                    cs.endText();

                    y -= 18;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText(String.format("Amount: $%.2f", svc.getHourlyRate()));
                    cs.endText();
                }

                y -= 18;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Scheduled at: " + when);
                cs.endText();
            }
            doc.save(file);
        } catch (IOException ex) {
            // Swallow silently or add a status label if desired
        }
    }
}

