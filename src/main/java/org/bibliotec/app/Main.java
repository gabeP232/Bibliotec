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
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        stage.setTitle("Bibliotec");
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("login.fxml"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}