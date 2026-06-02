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
import java.util.List;
import java.util.ResourceBundle;

public class Vue3Controller implements Initializable {

    private TierList tierList;

    @FXML
    private VBox unrankedArea;
    @FXML
    private TextField itemTextField;
    @FXML
    private Button finishButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button APIopt;

    private FlowPane itemsContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemsContainer = new FlowPane();
        itemsContainer.setHgap(10);
        itemsContainer.setVgap(10);
        unrankedArea.getChildren().add(itemsContainer);
    }

    public void afficherTierList(TierList tl) {
        this.tierList = tl;
        rafraichirPage();
    }

    @FXML
    private void ajoutItem() {
        String text = itemTextField.getText().trim();
        if (!text.isEmpty() && tierList != null) {
            Item newItem = new Item(text, false);
            tierList.addUnrankedItem(newItem);

            itemTextField.clear();
            rafraichirPage();
        }
    }

    @FXML
    private void ajoutImage() {
        if (tierList == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(unrankedArea.getScene().getWindow());

        if (selectedFile != null) {
            Item newImageItem = new Item(selectedFile.toURI().toString(), true);
            tierList.addUnrankedItem(newImageItem);

            rafraichirPage();
        }
    }

    @FXML
    private void boutonPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue1.fxml"));
            Parent root = loader.load();

            Vue1Controller controller = loader.getController();
            controller.envoyer(tierList);

            Scene scene = new Scene(root);
            Stage stage = (Stage) itemsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void boutonFinir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue1.fxml"));
            Parent root = loader.load();

            Vue1Controller controller = loader.getController();
            if (tierList != null) {
                controller.envoyer(tierList);
                DataManager.getInstance().saveConfig();
            }
            Scene scene = new Scene(root, 1293, 952);
            Stage stage = (Stage) itemsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void rafraichirPage() {
        if (tierList == null) return;

        itemsContainer.getChildren().clear();

        for (Item item : tierList.getUnrankedItems()) {
            javafx.scene.Node itemNode;

            if (item.isImage()) {
                ImageView imageView = new ImageView(new Image(item.getContent(), true));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                imageView.setStyle("-fx-border-radius: 8;");

                itemNode = imageView;
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

                itemNode = textLabel;
            }

            itemNode.setOnMouseClicked(event -> {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem deleteMenu = new MenuItem("Supprimer");

                deleteMenu.setOnAction(e -> suppItem(item));

                contextMenu.getItems().add(deleteMenu);

                contextMenu.show(itemNode, event.getScreenX(), event.getScreenY());
            });

            itemsContainer.getChildren().add(itemNode);
        }
    }

    private void fenetreAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void ajoutAPI() {
        if (tierList == null) return;

        List<String> choices = java.util.Arrays.asList("Film (TMDB)", "Jeu Vidéo (RAWG)");
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Film (TMDB)", choices);
        typeDialog.setTitle("Type de recherche");
        typeDialog.setHeaderText("Que voulez-vous ajouter ?");
        typeDialog.setContentText("Choisissez la catégorie :");

        typeDialog.showAndWait().ifPresent(selectedType -> {

            TextInputDialog textDialog = new TextInputDialog();
            textDialog.setTitle("Recherche " + selectedType);
            textDialog.setHeaderText("Recherche sur internet");
            textDialog.setContentText("Titre :");

            textDialog.showAndWait().ifPresent(title -> {
                if (!title.trim().isEmpty()) {
                    try {
                        AppConfig config = DataManager.getInstance().getConfig();
                        Item newItem = null;

                        if (selectedType.equals("Film (TMDB)")) {
                            String apiKey = (config != null) ? config.getTmdbApiKey() : null;
                            newItem = MultiApiManager.searchMovie(title, apiKey);
                        } else if (selectedType.equals("Jeu Vidéo (RAWG)")) {
                            String apiKey = (config != null) ? config.getRawgApiKey() : null;
                            newItem = MultiApiManager.searchGame(title, apiKey);
                        }

                        if (newItem != null) {
                            tierList.addUnrankedItem(newItem);
                            rafraichirPage();
                        }

                    } catch (Exception e) {
                        fenetreAlert(Alert.AlertType.ERROR, "Erreur de recherche", e.getMessage());
                    }
                }
            });
        });
    }

    @FXML
    public void suppItem(Item item){
        tierList.removeUnrankedItem(item);
        rafraichirPage();
    }

}