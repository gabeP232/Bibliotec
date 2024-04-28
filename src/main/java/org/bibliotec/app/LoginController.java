package org.bibliotec.app;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

import static javafx.beans.binding.Bindings.when;

public class LoginController {

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";

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

    public void login() throws SQLException, ClassNotFoundException {



        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", "An15no35gabe!");
        System.out.println(con);

        String username2 = username.getText();
        String password2 = password.getText();

        Statement stm = con.createStatement();
        String sql = "SELECT * FROM LOGIN where username ='" + username2 + "' and password='" + password2 + "'";
        ResultSet rs = stm.executeQuery(sql);

        if (rs.next()) {
            message.setVisible(false);
            homepage();
            return;
        }
        username.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        password.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        message.setVisible(true);
    }


        public void register() {
    }

    private void homepage() {
        Main.stage.setScene(HomeController.getScene());
    }
}