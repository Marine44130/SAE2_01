module org.example.sae2_01 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.sae2_01 to javafx.fxml;
    exports org.example.sae2_01;
}