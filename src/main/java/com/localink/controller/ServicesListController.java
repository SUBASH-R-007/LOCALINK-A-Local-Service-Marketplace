package com.localink.controller;

import com.localink.dao.ServiceDao;
import com.localink.model.Service;
import com.localink.util.Session;
import com.localink.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ServicesListController {
    @FXML private Label categoryTitle;
    @FXML private ListView<Service> servicesList;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        String cat = Session.getSelectedCategory();
        categoryTitle.setText(cat == null ? "Services" : cat);
        try {
            List<Service> services = new ServiceDao().listByCategory(cat);
            servicesList.setItems(FXCollections.observableArrayList(services));
            servicesList.setCellFactory(lv -> new ListCell<>() {
                private final Label title = new Label();
                private final Label desc = new Label();
                private final VBox textBox = new VBox(title, desc);
                private final Button bookBtn = new Button("Book");
                private final HBox row = new HBox(textBox, bookBtn);
                {
                    bookBtn.getStyleClass().add("primary");
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
        } catch (SQLException ex) {
            errorLabel.setText("Failed to load services");
        }
    }

    @FXML
    private void onBack() {
        ViewNavigator.navigate("/fxml/categories.fxml", "Localink - Browse Categories");
    }
}
