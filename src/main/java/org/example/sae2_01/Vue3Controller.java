package org.example.sae2_01;

package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Item;
import model.Tier;
import model.TierList;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur de la vue 3_2 : édition d'une tier-list.
 *
 * Responsabilités :
 *  - Afficher la zone "à classer" (items non encore classés)
 *  - Permettre l'ajout d'items texte ou image
 *  - Afficher les tiers existants et permettre d'y déposer des items (drag & drop)
 *  - Ajouter de nouveaux tiers (nom + couleur)
 *  - Naviguer vers la vue précédente ou terminer
 */
public class Vue3Controller implements Initializable {

    @FXML private VBox unrankedArea;

    @FXML private TextField itemTextField;
    @FXML private Button    addItemButton;
    @FXML private Button    addImageButton;

    @FXML private VBox tiersColumn1;
    @FXML private VBox tiersColumn2;
    @FXML private VBox tiersColumn3;

    @FXML private TextField tierNameField;
    @FXML private Button    colorButton;
    @FXML private Button    addTierButton;

    @FXML private Button previousButton;
    @FXML private Button finishButton;

    @FXML private ImageView menuIcon;
    @FXML private ImageView homeIcon;
    @FXML private ImageView themeIcon;
    @FXML private ImageView shareIcon;
    @FXML private ImageView saveIcon;

    private TierList tierList;

    private String selectedTierColor = "#858585";

    private Label draggedItemLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonActions();
    }

    public void setTierList(TierList tierList) {
        this.tierList = tierList;
        refreshUnrankedArea();
        refreshTiersGrid();
    }

    private void setupButtonActions() {
        if (addItemButton  != null) addItemButton .setOnAction(e -> handleAddTextItem());
        if (addImageButton != null) addImageButton.setOnAction(e -> handleAddImageItem());
        if (addTierButton  != null) addTierButton .setOnAction(e -> handleAddTier());
        if (colorButton    != null) colorButton   .setOnAction(e -> handleChooseColor());
        if (previousButton != null) previousButton.setOnAction(e -> handlePrevious());
        if (finishButton   != null) finishButton  .setOnAction(e -> handleFinish());

        if (saveIcon  != null) saveIcon .setOnMouseClicked(e -> handleSave());
        if (shareIcon != null) shareIcon.setOnMouseClicked(e -> handleShare());
        if (themeIcon != null) themeIcon.setOnMouseClicked(e -> handleToggleTheme());
        if (homeIcon  != null) homeIcon .setOnMouseClicked(e -> handleGoHome());
    }

    private void handleAddTextItem() {
        String text = itemTextField.getText().trim();
        if (text.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir un texte pour l'item.");
            return;
        }

        Item item = new Item(text);
        tierList.addUnrankedItem(item);
        itemTextField.clear();
        refreshUnrankedArea();
    }

    private void handleAddImageItem() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        Stage stage = (Stage) addImageButton.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        Item item = new Item(file.toURI().toString(), true); // item image
        tierList.addUnrankedItem(item);
        refreshUnrankedArea();
    }

    private void handleAddTier() {
        String name = tierNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir un nom pour le tier.");
            return;
        }

        Tier tier = new Tier(name, selectedTierColor);
        tierList.addTier(tier);
        tierNameField.clear();
        selectedTierColor = "#858585";
        colorButton.setStyle(colorButton.getStyle());
        refreshTiersGrid();
    }

    private void handleChooseColor() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choisir une couleur");
        dialog.setHeaderText("Couleur du tier :");

        ColorPicker picker = new ColorPicker(Color.web(selectedTierColor));
        dialog.getDialogPane().setContent(picker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Color c = picker.getValue();
                return String.format("#%02X%02X%02X",
                        (int)(c.getRed()   * 255),
                        (int)(c.getGreen() * 255),
                        (int)(c.getBlue()  * 255));
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hex -> {
            selectedTierColor = hex;
            colorButton.setStyle(colorButton.getStyle()
                    + "-fx-background-color: " + hex + ";");
        });
    }

    private void refreshUnrankedArea() {
        if (unrankedArea == null || tierList == null) return;
        unrankedArea.getChildren().clear();

        HBox currentRow = null;
        int count = 0;

        for (Item item : tierList.getUnrankedItems()) {
            if (count % 4 == 0) {           // 4 items par ligne
                currentRow = new HBox();
                currentRow.setPrefHeight(100);
                unrankedArea.getChildren().add(currentRow);
            }
            Label lbl = buildItemLabel(item);
            currentRow.getChildren().add(lbl);
            count++;
        }
    }

    private void refreshTiersGrid() {
        if (tiersColumn1 == null || tierList == null) return;

        VBox[] columns = {tiersColumn1, tiersColumn2, tiersColumn3};
        for (VBox col : columns) col.getChildren().clear();

        List<Tier> tiers = tierList.getTiers();
        for (int i = 0; i < tiers.size(); i++) {
            Tier tier = tiers.get(i);
            Label lbl = buildTierLabel(tier);
            columns[i % 3].getChildren().add(lbl);
        }
    }

    private Label buildItemLabel(Item item) {
        Label lbl = new Label();
        lbl.setPrefSize(80, 80);
        lbl.setAlignment(javafx.geometry.Pos.CENTER);
        lbl.setStyle("-fx-text-fill: white; -fx-background-radius: 15; -fx-background-color: #858585;");
        HBox.setMargin(lbl, new Insets(5, 10, 5, 10));

        if (item.isImage()) {
            ImageView iv = new ImageView(new Image(item.getContent(), 80, 80, true, true));
            lbl.setGraphic(iv);
        } else {
            lbl.setText(item.getContent());
        }

        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> {
            tierList.removeUnrankedItem(item);
            refreshUnrankedArea();
        });
        menu.getItems().add(deleteItem);
        lbl.setContextMenu(menu);

        lbl.setOnDragDetected(e -> {
            draggedItemLabel = lbl;
            Dragboard db = lbl.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(item.getContent()); // identifiant transporté
            db.setContent(cc);
            e.consume();
        });

        lbl.setOnDragDone(e -> draggedItemLabel = null);

        return lbl;
    }

    private Label buildTierLabel(Tier tier) {
        Label lbl = new Label(tier.getName());
        lbl.setPrefSize(100, 100);
        lbl.setAlignment(javafx.geometry.Pos.CENTER);
        lbl.setStyle(
                "-fx-border-color: white; -fx-text-fill: white;"
                        + "-fx-background-color: " + tier.getColor() + ";"
        );

        lbl.setOnDragOver(e -> {
            if (e.getGestureSource() != lbl && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        lbl.setOnDragEntered(e -> {
            lbl.setStyle(lbl.getStyle() + "-fx-border-width: 3;");
            e.consume();
        });

        lbl.setOnDragExited(e -> {
            lbl.setStyle(lbl.getStyle().replace("-fx-border-width: 3;", ""));
            e.consume();
        });

        lbl.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                String content = db.getString();
                tierList.getUnrankedItems().stream()
                        .filter(it -> it.getContent().equals(content))
                        .findFirst()
                        .ifPresent(it -> {
                            tierList.removeUnrankedItem(it);
                            tier.addItem(it);
                            refreshUnrankedArea();
                            refreshTiersGrid();
                        });
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }
            e.consume();
        });

        ContextMenu menu = new ContextMenu();
        MenuItem renameItem = new MenuItem("Renommer");
        renameItem.setOnAction(e -> handleRenameTier(tier));
        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> {
            tierList.removeTier(tier);
            refreshTiersGrid();
        });
        menu.getItems().addAll(renameItem, deleteItem);
        lbl.setContextMenu(menu);

        return lbl;
    }

    private void handleRenameTier(Tier tier) {
        TextInputDialog dialog = new TextInputDialog(tier.getName());
        dialog.setTitle("Renommer le tier");
        dialog.setHeaderText("Nouveau nom :");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                tier.setName(name);
                refreshTiersGrid();
            }
        });
    }

    private void handleSave() {
        // TODO : sérialiser tierList dans un fichier binaire (ObjectOutputStream)
        showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Tier-list sauvegardée.");
    }

    private void handleShare() {
        // TODO : export (JSON / image snapshot)
        showAlert(Alert.AlertType.INFORMATION, "Partage", "Fonctionnalité d'export à venir.");
    }

    private void handleToggleTheme() {
        // TODO : basculer entre thème clair et sombre via une CSS alternée
    }

    private void handleGoHome() {
        // TODO : naviguer vers la vue d'accueil
    }

    private void handlePrevious() {
        // TODO : revenir à la vue précédente (ex. choix du nom de la tier-list)
    }

    private void handleFinish() {
        // TODO : valider et sauvegarder, puis naviguer vers la vue de visualisation
        handleSave();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}