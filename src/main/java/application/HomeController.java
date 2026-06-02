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

    @FXML
    private Button importer_btn;
    @FXML
    private Button creer_btn;

    @FXML
    private TextField apiKeyField;

    @FXML
    private TextField apiKeyField1;

    @FXML
    private Button saveApi_btn;

    @FXML
    private Button saveApi_btn1;

    @FXML
    public void initialize() {
        AppConfig config = DataManager.getInstance().getConfig();
        if (config != null) {
            if (config.getRawgApiKey() != null) {
                apiKeyField.setText(config.getRawgApiKey());
            }
            if (config.getTmdbApiKey() != null) {
                apiKeyField1.setText(config.getTmdbApiKey());
            }
        }
    }

    @FXML
    public void handleSaveApiBtn() {
        AppConfig config = DataManager.getInstance().getConfig();
        if (config == null) {
            config = new AppConfig();
        }

        if (apiKeyField != null && !apiKeyField.getText().isBlank()) {
            config.setRawgApiKey(apiKeyField.getText().trim());
        }

        if (apiKeyField1 != null && !apiKeyField1.getText().isBlank()) {
            config.setTmdbApiKey(apiKeyField1.getText().trim());
        }

        DataManager.getInstance().saveConfig();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuration");
        alert.setHeaderText(null);
        alert.setContentText("Vos clés API (TMDB et RAWG) ont été sauvegardées avec succès !");
        alert.showAndWait();
    }

    @FXML
    public void handleCreerBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vue1.fxml"));
        Parent root = loader.load();

        Vue1Controller controller = loader.getController();

        controller.envoyer(new TierList("sans nom"));
        DataManager.getInstance().saveConfig();

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

                FXMLLoader loader = new FXMLLoader(getClass().getResource("vue1.fxml"));
                Parent root = loader.load();

                Vue1Controller controller = loader.getController();
                controller.envoyer(loadedList);

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

    @FXML
    public void handlehome_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vue2.fxml"));
        Parent root = loader.load();

        Vue2Controller controller = loader.getController();
        controller.afficherListDansMesList();

        Scene scene = new Scene(root, 550, 700);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}