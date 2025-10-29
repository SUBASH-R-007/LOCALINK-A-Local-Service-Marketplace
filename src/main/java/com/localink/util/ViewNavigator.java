package com.localink.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ViewNavigator {
    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(640);
        primaryStage.setResizable(true);
    }

    public static void navigate(String fxmlPath, String title) {
        try {
            boolean wasMaximized = primaryStage.isMaximized();
            boolean wasFullScreen = primaryStage.isFullScreen();
            double prevW = primaryStage.getWidth();
            double prevH = primaryStage.getHeight();
            double prevX = primaryStage.getX();
            double prevY = primaryStage.getY();
            URL fxml = Objects.requireNonNull(ViewNavigator.class.getResource(fxmlPath));
            Parent root = FXMLLoader.load(fxml);
            // If possible, set the root's preferred size to previous bounds to prevent stage from shrinking
            if (root instanceof Region && prevW > 0 && prevH > 0) {
                Region r = (Region) root;
                r.setPrefSize(prevW, prevH);
            }
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(ViewNavigator.class.getResource("/styles/app.css")).toExternalForm());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            // Reapply state in priority order: Fullscreen > Maximized > Bounds
            if (wasFullScreen) {
                primaryStage.setFullScreen(true);
                primaryStage.show();
            } else if (wasMaximized) {
                primaryStage.setMaximized(true);
                primaryStage.show();
            } else {
                if (prevW > 0 && prevH > 0) {
                    primaryStage.setWidth(prevW);
                    primaryStage.setHeight(prevH);
                }
                if (!Double.isNaN(prevX) && !Double.isNaN(prevY)) {
                    primaryStage.setX(prevX);
                    primaryStage.setY(prevY);
                }
                primaryStage.show();
            }

            // Some platforms may still apply the Scene's preferred size after show();
            // reapply in the next pulse to guarantee final bounds/state.
            Platform.runLater(() -> {
                if (wasFullScreen) {
                    primaryStage.setFullScreen(true);
                } else if (wasMaximized) {
                    primaryStage.setMaximized(true);
                } else {
                    if (prevW > 0 && prevH > 0) {
                        primaryStage.setWidth(prevW);
                        primaryStage.setHeight(prevH);
                    }
                    if (!Double.isNaN(prevX) && !Double.isNaN(prevY)) {
                        primaryStage.setX(prevX);
                        primaryStage.setY(prevY);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}
