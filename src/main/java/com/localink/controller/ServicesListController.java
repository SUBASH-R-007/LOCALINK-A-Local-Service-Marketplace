package com.localink.controller;

import com.localink.dao.ServiceDao;
import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.sql.SQLException;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ServicesListController {
    @FXML private Label categoryTitle;
    @FXML private ListView<Service> servicesList;
    @FXML private Label errorLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortCombo;

    private ObservableList<Service> allServices = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        String cat = Session.getSelectedCategory();
        categoryTitle.setText(cat == null ? "Services" : cat);
        try {
            List<Service> services = (cat == null || cat.isBlank())
                    ? new ServiceDao().listAllActive()
                    : new ServiceDao().listByCategory(cat);
            allServices.setAll(services);
            servicesList.setItems(FXCollections.observableArrayList(allServices));
            servicesList.setCellFactory(lv -> new ListCell<>() {
                private final Label title = new Label();
                private final Label desc = new Label();
                private final VBox textBox = new VBox(title, desc);
                private final Button bookBtn = new Button("Book");
                private final HBox row = new HBox(textBox, bookBtn);
                {
                    bookBtn.getStyleClass().add("primary");
                    bookBtn.setMinWidth(90);
                    title.setWrapText(true);
                    desc.setWrapText(true);
                    textBox.setFillWidth(true);
                    row.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(textBox, Priority.ALWAYS);
                    row.setSpacing(12);
                }
                @Override protected void updateItem(Service item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        bookBtn.setOnAction(null);
                    } else {
                        title.setText(item.getTitle() + " — $" + item.getHourlyRate() + "/h");
                        desc.setText(item.getDescription());
                        bookBtn.setOnAction(e -> {
                            Session.setSelectedService(item);
                            ViewNavigator.navigate("/fxml/booking.fxml", "Localink - Booking");
                        });
                        setGraphic(row);
                        setText(null);
                    }
                }
            });
            // Double-click to open booking as well
            servicesList.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2) {
                    Service sel = servicesList.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        Session.setSelectedService(sel);
                        ViewNavigator.navigate("/fxml/booking.fxml", "Localink - Booking");
                    }
                }
            });

            if (searchField != null) {
                searchField.textProperty().addListener((obs, old, val) -> applyFilters());
            }
            if (sortCombo != null) {
                sortCombo.getItems().setAll("Title (A-Z)", "Rate (Low-High)", "Rate (High-Low)");
                sortCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> applyFilters());
            }
            applyFilters();
        } catch (SQLException ex) {
            errorLabel.setText("Failed to load services");
        }
    }

    @FXML
    private void onBack() {
        ViewNavigator.navigate("/fxml/categories.fxml", "Localink - Browse Categories");
    }

    private void applyFilters() {
        String q = searchField != null ? searchField.getText() : null;
        String sort = sortCombo != null ? sortCombo.getSelectionModel().getSelectedItem() : null;

        List<Service> filtered = allServices.stream()
                .filter(s -> {
                    if (q == null || q.isBlank()) return true;
                    String t = q.toLowerCase();
                    return (s.getTitle() != null && s.getTitle().toLowerCase().contains(t))
                            || (s.getDescription() != null && s.getDescription().toLowerCase().contains(t));
                })
                .collect(Collectors.toList());

        if (sort != null) {
            switch (sort) {
                case "Title (A-Z)":
                    filtered.sort(Comparator.comparing(Service::getTitle, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Rate (Low-High)":
                    filtered.sort(Comparator.comparingDouble(Service::getHourlyRate));
                    break;
                case "Rate (High-Low)":
                    filtered.sort(Comparator.comparingDouble(Service::getHourlyRate).reversed());
                    break;
                default:
                    break;
            }
        }

        servicesList.setItems(FXCollections.observableArrayList(filtered));
    }
}

