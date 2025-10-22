package com.localink;

import com.localink.util.ViewNavigator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        ViewNavigator.init(stage);
        ViewNavigator.navigate("/fxml/login.fxml", "Localink - Login");
    }
}
