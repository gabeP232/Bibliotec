package org.bibliotec.app;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage stage) {
        Main.stage = stage;
        stage.setTitle("Bibliotec");
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        HomeController.show();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}