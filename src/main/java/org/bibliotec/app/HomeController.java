package org.bibliotec.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.UncheckedIOException;

public class HomeController {

    private static Scene scene;

    public static Scene getScene() {
        if (scene == null) {
            try {
                scene = new Scene(FXMLLoader.load(HomeController.class.getResource("home.fxml")));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return scene;
    }

    public void logout() {
    }

    public void books() {
    }

    public void users() {
    }

    public void loans() {

    }
}
