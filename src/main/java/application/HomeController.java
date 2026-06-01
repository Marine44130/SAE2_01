package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML Button importer_btn;
    @FXML Button creer_btn;
    @FXML Button home_btn;

    @FXML TextField apiKeyField;
    @FXML Button saveApi_btn;

    @FXML
    public void initialize() {
        String savedKey = DataManager.getInstance().getConfig().getTmdbApiKey();
        if (savedKey != null) {
            apiKeyField.setText(savedKey);
        }
    }

    @FXML
    public void handleSaveApiBtn() {
        String key = apiKeyField.getText();
        if (key != null && !key.isBlank()) {
            DataManager.getInstance().getConfig().setTmdbApiKey(key.trim());
            DataManager.getInstance().saveConfig();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Clé API enregistrée avec succès !");
            alert.setHeaderText(null);
            alert.show();
        }
    }

    @FXML
    public void handlehome_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vue2.fxml"));
        Parent root = loader.load();
        Vue2Controller controller = loader.getController();
        controller.afficherList();
        Scene scene = new Scene(root, 550, 700);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}