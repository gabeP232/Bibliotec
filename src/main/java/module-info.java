module org.example.bibliotec {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.bibliotec to javafx.fxml;
    exports org.example.bibliotec;
}