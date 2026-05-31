module org.example.sae2_01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires okhttp3;
    requires com.google.gson;


    opens application to javafx.fxml;
    exports application;
}