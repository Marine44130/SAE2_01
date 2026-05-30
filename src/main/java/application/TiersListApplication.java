package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TiersListApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TiersListApplication.class.getResource("vue3.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 700);
        stage.setTitle("TiersList Creator");
        stage.setScene(scene);
        stage.show();
    }
}
