package org.bibliotec.app;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bibliotec.app.DatabaseAccess.User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static javafx.beans.binding.Bindings.when;

public class LoginController {

    private static Scene scene;

    @FXML private TextField username;
    @FXML private PasswordField password, confirmPassword;
    @FXML private Message errorMessage;
    @FXML private Button login, register;
    private final SimpleBooleanProperty registering = new SimpleBooleanProperty();

    public static void show() {
        if (scene == null) {
            try {
                scene = new Scene(FXMLLoader.load(LoginController.class.getResource("login.fxml")));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        Main.stage.setScene(scene);
    }

    public void initialize() {
        registering.addListener(__ -> {
            errorMessage.setVisible(false);
            List.of(username, password, confirmPassword).forEach(node -> setRedHighlight(node, false));
        });
        register.textProperty().bind(when(registering).then("Confirm Registration").otherwise("Register"));
        login.textProperty().bind(when(registering).then("Cancel").otherwise("Login"));
        confirmPassword.visibleProperty().bind(registering);
        confirmPassword.managedProperty().bind(confirmPassword.visibleProperty());
        errorMessage.managedProperty().bind(errorMessage.visibleProperty());
        var usernameInvalid = username.textProperty().length().lessThan(4);
        var passwordInvalid = password.textProperty().length().lessThan(8);
        var confirmPasswordInvalid = confirmPassword.textProperty().isNotEqualTo(password.textProperty());

        errorMessage.descriptionProperty().bind(
                when(registering).then(Bindings.concat(
                        when(usernameInvalid).then("Username must be at least 4 characters.").otherwise(""),
                        when(usernameInvalid.and(passwordInvalid)).then("\n").otherwise(""),
                        when(passwordInvalid).then("Password must be at least 8 characters.").otherwise(""),
                        when(usernameInvalid.or(passwordInvalid).and(confirmPasswordInvalid)).then("\n").otherwise(""),
                        when(confirmPasswordInvalid).then("Passwords must match.").otherwise("")
                )).otherwise(
                        "Invalid Credentials."
                ));
    }

    public void login() {
        var result = DatabaseAccess.login(username.getText(), password.getText());
        errorMessage.setVisible(result.isEmpty());
        result.ifPresent(x -> HomeController.show());
    }

    public void register() {
        if (registering.get()) {
            boolean failed = false;
            if (username.getText().length() < 4) {
                setRedHighlight(username, true);
                failed = true;
            }
            if (password.getText().length() < 8) {
                setRedHighlight(password, true);
                failed = true;
            }
            if (!confirmPassword.getText().equals(password.getText())) {
                setRedHighlight(confirmPassword, true);
                failed = true;
            }
            errorMessage.setVisible(failed);
            if (!failed) {
                DatabaseAccess.addPatron(new User(username.getText(), "full name", "email", "address", password.getText(), false));
                registering.set(false);
                HomeController.show();
            }
        } else {
            registering.set(true);
        }
    }

    private static void setRedHighlight(Node node, boolean highlight) {
        node.pseudoClassStateChanged(Styles.STATE_DANGER, highlight);
    }

}