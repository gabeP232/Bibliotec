package org.bibliotec.app;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler(Main::uncaughtException);
        Thread.currentThread().setUncaughtExceptionHandler(Main::uncaughtException);

        Main.stage = stage;
        stage.setTitle("Bibliotec");
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        HomeController.show();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void uncaughtException(Thread __, Throwable e) {
        try {
            while (e != null && e.getCause() != null && (e instanceof InvocationTargetException || e.getCause() instanceof InvocationTargetException
                    || e instanceof UncheckedIOException
                    || e.getClass().equals(RuntimeException.class) && (e.getMessage() == null || e.getMessage().equals(e.getCause().toString())))) {
                e = e.getCause();
            }
            e.printStackTrace();
            var text = new StringWriter();
            e.printStackTrace(new PrintWriter(text));
            var area = new TextArea(text.toString());
            area.setEditable(false);
            String content = e.getMessage() == null ? e.toString() : e.getMessage();
            Runnable show = () -> {
                try {
                    var alert = new Alert(Alert.AlertType.ERROR, content);
                    alert.getDialogPane().setExpandableContent(area);
                    alert.show();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            };
            if (Platform.isFxApplicationThread())
                show.run();
            else
                Platform.runLater(show);
        } catch (Throwable ex) {
            // don't go into an infinite loop
            ex.printStackTrace();
        }
    }
}