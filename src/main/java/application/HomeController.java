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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class HomeController {

    @FXML private Button importer_btn;
    @FXML private Button creer_btn;
    @FXML private TextField apiKeyField;
    @FXML private Button saveApi_btn;

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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Configuration");
            alert.setHeaderText(null);
            alert.setContentText("Clé API sauvegardée avec succès !");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleCreerBtn(ActionEvent event) throws IOException {
        // CORRECTION : Redirige correctement vers l'écran de création (vue2.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vue2.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 550, 700);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleImporterBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une sauvegarde binaire");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Tier-List (*.ser)", "*.ser"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                TierList loadedList = (TierList) ois.readObject();
                DataManager.getInstance().addTierList(loadedList);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("vue3.fxml"));
                Parent root = loader.load();
                Vue3Controller controller = loader.getController();
                controller.setTierList(loadedList);

                stage.setScene(new Scene(root, 550, 700));
                stage.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'importation");
                alert.setContentText("Impossible de charger le fichier sélectionné.");
                alert.showAndWait();
            }
        }
    }
}