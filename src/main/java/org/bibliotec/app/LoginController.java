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

    @FXML private TextField username, fullName, email, address;
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
            List.of(username, fullName, password, confirmPassword, email, address).forEach(node -> Utils.setRedHighlight(node, false));
        });
        register.textProperty().bind(when(registering).then("Confirm Registration").otherwise("Register"));
        login.textProperty().bind(when(registering).then("Cancel").otherwise("Login"));
        confirmPassword.visibleProperty().bind(registering);
        confirmPassword.managedProperty().bind(confirmPassword.visibleProperty());
        email.visibleProperty().bind(registering);
        email.managedProperty().bind(email.visibleProperty());
        address.visibleProperty().bind(registering);
        address.managedProperty().bind(address.visibleProperty());
        fullName.visibleProperty().bind(registering);
        fullName.managedProperty().bind(fullName.visibleProperty());

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
        if (registering.get()) {
            registering.set(false);
        } else {
            var result = DatabaseAccess.login(username.getText(), password.getText());
            errorMessage.setVisible(result.isEmpty());
            result.ifPresent(x -> {
                if (x.isAdmin()) {
                    AdminController.show();
                } else {
                    PatronController.show(x.userID());
                }
            });
        }
    }

    public void register() {
        if (registering.get()) {
            boolean failed = false;
            if (username.getText().length() < 4) {
                Utils.setRedHighlight(username, true);
                failed = true;
            }
            if (password.getText().length() < 8) {
                Utils.setRedHighlight(password, true);
                failed = true;
            }
            if (!confirmPassword.getText().equals(password.getText())) {
                Utils.setRedHighlight(confirmPassword, true);
                failed = true;
            }
            errorMessage.setVisible(failed);
            if (!failed) {
                var user = new User(username.getText(), fullName.getText(), email.getText(), address.getText(), password.getText(), false);
                DatabaseAccess.addPatron(user);
                registering.set(false);
                PatronController.show(user.userID());
            }
        } else {
            registering.set(true);
        }
    }



}