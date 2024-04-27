package org.bibliotec.app;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static javafx.beans.binding.Bindings.when;

public class LoginController {

    public TextField username;
    public PasswordField password;
    public Message message;

    public void initialize() {
        message.managedProperty().bind(message.visibleProperty());
        message.descriptionProperty().bind(Bindings.concat(
                "Invalid Credentials.",
                when(username.textProperty().isEmpty().or(password.textProperty().isEmpty())).then("\n").otherwise(""),
                when(username.textProperty().isEmpty()).then("Username is required.").otherwise(""),
                when(username.textProperty().isEmpty().and(password.textProperty().isEmpty())).then("\n").otherwise(""),
                when(password.textProperty().isEmpty()).then("Password is required.").otherwise("")
        ));
    }

    public void login() {
        System.out.println("Login button clicked!");
        username.pseudoClassStateChanged(Styles.STATE_DANGER, username.getText().isEmpty());
        password.pseudoClassStateChanged(Styles.STATE_DANGER, password.getText().isEmpty());
        message.setVisible(username.getText().isEmpty() || password.getText().isEmpty());
    }

    public void register() {
    }
}