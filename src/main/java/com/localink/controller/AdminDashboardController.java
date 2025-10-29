package com.localink.controller;

import com.localink.util.ViewNavigator;
import com.localink.util.Session;
import com.localink.dao.UserDao;
import com.localink.dao.ServiceDao;
import com.localink.dao.BookingDao;
import com.localink.model.User;
import com.localink.model.Service;
import com.localink.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;

public class AdminDashboardController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> userColId;
    @FXML private TableColumn<User, String> userColName;
    @FXML private TableColumn<User, String> userColEmail;
    @FXML private TableColumn<User, String> userColRole;
    @FXML private TableColumn<User, Object> userColCreated;

    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Long> svcColId;
    @FXML private TableColumn<Service, String> svcColTitle;
    @FXML private TableColumn<Service, String> svcColCategory;
    @FXML private TableColumn<Service, String> svcColProvider;
    @FXML private TableColumn<Service, Double> svcColRate;
    @FXML private TableColumn<Service, Boolean> svcColActive;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Long> bkColId;
    @FXML private TableColumn<Booking, Long> bkColCustomer;
    @FXML private TableColumn<Booking, Long> bkColService;
    @FXML private TableColumn<Booking, String> bkColStatus;
    @FXML private TableColumn<Booking, Object> bkColWhen;

    @FXML
    public void initialize() {
        try {
            // Users
            userColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            userColName.setCellValueFactory(new PropertyValueFactory<>("name"));
            userColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            userColRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            userColCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            ObservableList<User> users = FXCollections.observableArrayList(new UserDao().listAll());
            usersTable.setItems(users);
            usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            usersTable.refresh();
            System.out.println("[Admin] Users loaded: " + users.size());
            usersTable.setPlaceholder(new Label("No users (" + users.size() + ")"));

            // Services (active only for now)
            svcColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            svcColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            svcColCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            svcColProvider.setCellValueFactory(new PropertyValueFactory<>("providerName"));
            svcColRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
            svcColActive.setCellValueFactory(new PropertyValueFactory<>("active"));
            ObservableList<Service> services = FXCollections.observableArrayList(new ServiceDao().listAllActive());
            servicesTable.setItems(services);
            servicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            servicesTable.refresh();
            System.out.println("[Admin] Services loaded: " + services.size());
            servicesTable.setPlaceholder(new Label("No services (" + services.size() + ")"));

            // Bookings
            bkColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            bkColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            bkColService.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
            bkColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            bkColWhen.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
            ObservableList<Booking> bookings = FXCollections.observableArrayList(new BookingDao().listAll());
            bookingsTable.setItems(bookings);
            bookingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            bookingsTable.refresh();
            System.out.println("[Admin] Bookings loaded: " + bookings.size());
            bookingsTable.setPlaceholder(new Label("No bookings (" + bookings.size() + ")"));
        } catch (Exception e) {
            e.printStackTrace();
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
