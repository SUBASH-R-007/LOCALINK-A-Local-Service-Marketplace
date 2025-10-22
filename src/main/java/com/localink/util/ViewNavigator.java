package com.localink.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ViewNavigator {
    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(640);
    }

    public static void navigate(String fxmlPath, String title) {
        try {
            URL fxml = Objects.requireNonNull(ViewNavigator.class.getResource(fxmlPath));
            Parent root = FXMLLoader.load(fxml);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(ViewNavigator.class.getResource("/styles/app.css")).toExternalForm());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}
