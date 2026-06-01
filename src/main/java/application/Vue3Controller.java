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

    // Éléments FXML reliés strictement à vue3.fxml
    @FXML private VBox unrankedArea;
    @FXML private TextField itemTextField;
    @FXML private Button finishButton;
    @FXML private Button previousButton;

    // Un conteneur FlowPane pour que les carrés 100x100 s'alignent proprement
    private FlowPane itemsContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Au lancement, on crée le FlowPane et on le met dans la VBox
        itemsContainer = new FlowPane();
        itemsContainer.setHgap(10); // Espace horizontal entre les carrés
        itemsContainer.setVgap(10); // Espace vertical entre les carrés
        unrankedArea.getChildren().add(itemsContainer);
    }

    /**
     * Réception de la TierList depuis la Vue 2
     */
    public void setTierList(TierList tl) {
        this.currentTierList = tl;
        refreshUI();
    }

    /**
     * Action : Ajouter un texte
     */
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

    /**
     * Action : Ajouter une image
     */
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

    /**
     * Action : Bouton Précédent
     */
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

    /**
     * Action : Bouton Finir
     */
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

    /**
     * Met à jour l'affichage avec des éléments de 100x100
     */
    private void refreshUI() {
        if (currentTierList == null) return;

        itemsContainer.getChildren().clear();

        for (Item item : currentTierList.getUnrankedItems()) {
            if (item.isImage()) {
                // Créer une image 100x100
                ImageView imageView = new ImageView(new Image(item.getContent(), true));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                imageView.setStyle(
                        "-fx-border-radius: 8;"
                );// Force le format carré 100x100

                itemsContainer.getChildren().add(imageView);
            } else {
                // Créer un label (carré de texte) 100x100
                Label textLabel = new Label(item.getContent());
                textLabel.setPrefSize(100, 100);
                textLabel.setMinSize(100, 100);
                textLabel.setMaxSize(100, 100);
                textLabel.setAlignment(Pos.CENTER);
                textLabel.setWrapText(true); // Retour à la ligne automatique

                // Style graphique du carré
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
}