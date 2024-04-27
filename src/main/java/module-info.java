module org.bibliotec.app {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.bibliotec.app to javafx.fxml;
    exports org.bibliotec.app;
}