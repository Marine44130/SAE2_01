package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Vue3Controller implements Initializable {

    private TierList currentTierList;

    @FXML private VBox unrankedArea;
    @FXML private TextField itemTextField;
    @FXML private Button finishButton;
    @FXML private Button previousButton;
    @FXML private Button APIopt; // Le bouton API

    private FlowPane itemsContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemsContainer = new FlowPane();
        itemsContainer.setHgap(10);
        itemsContainer.setVgap(10);
        unrankedArea.getChildren().add(itemsContainer);
    }

    public void setTierList(TierList tl) {
        this.currentTierList = tl;
        refreshUI();
    }

    @FXML
    private void handleAddItem() {
        String text = itemTextField.getText().trim();
        if (!text.isEmpty() && currentTierList != null) {
            Item newItem = new Item(text, false);
            currentTierList.addUnrankedItem(newItem);

            itemTextField.clear();
            refreshUI();
        }
    }

    @FXML
    private void handleAddImage() {
        if (currentTierList == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(unrankedArea.getScene().getWindow());

        if (selectedFile != null) {
            Item newImageItem = new Item(selectedFile.toURI().toString(), true);
            currentTierList.addUnrankedItem(newImageItem);

            refreshUI();
        }
    }

    @FXML
    private void handleAddMovieApi() {
        if (currentTierList == null) return;

        AppConfig config = DataManager.getInstance().getConfig();
        String apiKey = (config != null) ? config.getTmdbApiKey() : null;

        if (apiKey == null || apiKey.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Clé API manquante", "Veuillez configurer votre clé API dans l'accueil (Vue 1) avant d'utiliser cette fonction.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche TMDB");
        dialog.setHeaderText("Rechercher via l'API");
        dialog.setContentText("Recherche de votre theme API :");

        dialog.showAndWait().ifPresent(title -> {
            if (!title.trim().isEmpty()) {
                try {
                    Item movieItem = TMDBApiManager.searchMovieAsItem(title.trim(), apiKey);
                    currentTierList.addUnrankedItem(movieItem);
                    refreshUI();

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de recherche", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handlePrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue2.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) previousButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFinish() {
        try {
            if (currentTierList != null) {
                DataManager.getInstance().saveConfig();
            }
            Parent root = FXMLLoader.load(getClass().getResource("vue1.fxml"));
            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshUI() {
        if (currentTierList == null) return;

        itemsContainer.getChildren().clear();

        for (Item item : currentTierList.getUnrankedItems()) {
            if (item.isImage()) {
                ImageView imageView = new ImageView(new Image(item.getContent(), true));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                imageView.setStyle("-fx-border-radius: 8;");

                itemsContainer.getChildren().add(imageView);
            } else {
                Label textLabel = new Label(item.getContent());
                textLabel.setPrefSize(100, 100);
                textLabel.setMinSize(100, 100);
                textLabel.setMaxSize(100, 100);
                textLabel.setAlignment(Pos.CENTER);
                textLabel.setWrapText(true);

                textLabel.setStyle(
                        "-fx-background-color: #616161; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 8; " +
                                "-fx-border-color: #888888; " +
                                "-fx-border-radius: 8;"
                );

                itemsContainer.getChildren().add(textLabel);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}