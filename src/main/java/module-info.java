module com.example.uap_proglan {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uap_proglan to javafx.fxml;
    exports com.example.uap_proglan;
}