package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Vue3Controller implements Initializable {

    private TierList currentTierList;

    @FXML private VBox unrankedArea;
    @FXML private TextField itemTextField;
    @FXML private Button addItemButton;
    @FXML private Button addImageButton;
    @FXML private Button addMovieApiButton;

    @FXML private VBox tiersColumn1;
    @FXML private VBox tiersColumn2;
    @FXML private VBox tiersColumn3;

    @FXML private TextField tierNameField;
    @FXML private Button colorButton;
    @FXML private Button addTierButton;
    @FXML private Button previousButton;
    @FXML private Button finishButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addItemButton.setOnAction(e -> handleAddItem());
        addTierButton.setOnAction(e -> handleAddTier());
        addMovieApiButton.setOnAction(e -> handleAddMovieApi());
        previousButton.setOnAction(e -> handleGoHome());
        finishButton.setOnAction(e -> handleSave());
    }

    public void setTierList(TierList tl) {
        this.currentTierList = tl;
        refreshTiersGrid();
        refreshUnrankedArea();
    }

    private void refreshTiersGrid() {
        tiersColumn1.getChildren().clear();
        tiersColumn2.getChildren().clear();
        tiersColumn3.getChildren().clear();

        if (currentTierList == null) return;

        for (Tier tier : currentTierList.getTiers()) {
            double hauteur = 110.0;

            Label lblTier = new Label(tier.getName());
            lblTier.setPrefSize(80, hauteur);
            lblTier.setMinHeight(hauteur);
            lblTier.setAlignment(Pos.CENTER);
            lblTier.setStyle("-fx-background-color: " + tier.getColor() + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 18; -fx-border-color: #1e1e1e;");

            ContextMenu tierMenu = new ContextMenu();
            MenuItem renameItem = new MenuItem("Renommer");
            renameItem.setOnAction(e -> handleRenameTier(tier));
            MenuItem changeColorItem = new MenuItem("Changer la couleur");
            changeColorItem.setOnAction(e -> handleColorTier(tier));
            MenuItem deleteTier = new MenuItem("Supprimer le Tier");
            deleteTier.setOnAction(e -> {
                for (Item i : tier.getItems()) {
                    i.setTier(null);
                    currentTierList.addUnrankedItem(i);
                }
                currentTierList.removeTier(tier);
                refreshTiersGrid();
                refreshUnrankedArea();
            });
            tierMenu.getItems().addAll(renameItem, changeColorItem, deleteTier);
            lblTier.setContextMenu(tierMenu);

            lblTier.setOnDragDetected(event -> {
                Dragboard db = lblTier.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("TIER:" + tier.getName());
                db.setContent(content);
                event.consume();
            });

            lblTier.setOnDragOver(event -> {
                if (event.getGestureSource() != lblTier && event.getDragboard().hasString() && event.getDragboard().getString().startsWith("TIER:")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            lblTier.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString() && db.getString().startsWith("TIER:")) {
                    String sourceName = db.getString().substring(5);
                    Tier sourceTier = findTierByName(sourceName);
                    if (sourceTier != null && sourceTier != tier) {
                        int oldPlace = sourceTier.getPlace();
                        sourceTier.setPlace(tier.getPlace());
                        tier.setPlace(oldPlace);
                        currentTierList.tri();
                        refreshTiersGrid();
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            tiersColumn1.getChildren().add(lblTier);

            FlowPane itemsRow = new FlowPane();
            itemsRow.setHgap(10);
            itemsRow.setVgap(10);
            itemsRow.setPrefHeight(hauteur);
            itemsRow.setMinHeight(hauteur);
            itemsRow.setStyle("-fx-background-color: #3a3a3a; -fx-border-color: #1e1e1e; -fx-padding: 5;");

            itemsRow.setOnDragOver(event -> {
                if (event.getGestureSource() != itemsRow && event.getDragboard().hasString() && !event.getDragboard().getString().startsWith("TIER:")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            itemsRow.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString() && !db.getString().startsWith("TIER:")) {
                    Item dragItem = findItemByContent(db.getString());
                    if (dragItem != null) {
                        if (dragItem.getTier() != null) dragItem.getTier().removeItem(dragItem);
                        else currentTierList.removeUnrankedItem(dragItem);

                        tier.addItem(dragItem);
                        dragItem.setTier(tier);
                        refreshTiersGrid();
                        refreshUnrankedArea();
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            for (Item item : tier.getItems()) {
                itemsRow.getChildren().add(createItemNode(item));
            }
            tiersColumn2.getChildren().add(itemsRow);

            VBox controlsRow = new VBox(5);
            controlsRow.setAlignment(Pos.CENTER);
            controlsRow.setPrefHeight(hauteur);
            controlsRow.setMinHeight(hauteur);
            controlsRow.setStyle("-fx-border-color: #1e1e1e;");

            Button upBtn = new Button("▲");
            Button downBtn = new Button("▼");
            upBtn.setStyle("-fx-background-color: #525252; -fx-text-fill: white; -fx-font-size: 10;");
            downBtn.setStyle("-fx-background-color: #525252; -fx-text-fill: white; -fx-font-size: 10;");

            upBtn.setOnAction(e -> {
                int index = currentTierList.getTiers().indexOf(tier);
                if (index > 0) {
                    tier.setPlace(index - 1);
                    currentTierList.getTiers().get(index - 1).setPlace(index);
                    currentTierList.tri();
                    refreshTiersGrid();
                }
            });

            controlsRow.getChildren().addAll(upBtn, downBtn);
            tiersColumn3.getChildren().add(controlsRow);
        }
    }

    private void refreshUnrankedArea() {
        unrankedArea.getChildren().clear();
        if (currentTierList == null) return;

        FlowPane unrankedContainer = new FlowPane();
        unrankedContainer.setHgap(10);
        unrankedContainer.setVgap(10);
        unrankedContainer.setPadding(new Insets(10));
        unrankedContainer.setStyle("-fx-background-color: transparent;");

        unrankedContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != unrankedContainer && event.getDragboard().hasString() && !event.getDragboard().getString().startsWith("TIER:")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        unrankedContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && !db.getString().startsWith("TIER:")) {
                Item dragItem = findItemByContent(db.getString());
                if (dragItem != null) {
                    if (dragItem.getTier() != null) {
                        dragItem.getTier().removeItem(dragItem);
                        dragItem.setTier(null);
                    }
                    currentTierList.addUnrankedItem(dragItem);
                    refreshTiersGrid();
                    refreshUnrankedArea();
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        for (Item item : currentTierList.getUnrankedItems()) {
            unrankedContainer.getChildren().add(createItemNode(item));
        }
        unrankedArea.getChildren().add(unrankedContainer);
    }

    private Node createItemNode(Item item) {
        double taille = 100.0;
        StackPane itemContainer = new StackPane();
        itemContainer.setPrefSize(taille, taille);
        itemContainer.setMinSize(taille, taille);
        itemContainer.setMaxSize(taille, taille);
        itemContainer.setStyle("-fx-background-color: #8f8f8f; -fx-background-radius: 15; -fx-border-color: #a7a7a7; -fx-border-radius: 15;");

        if (item.isImage()) {
            ImageView iv = new ImageView(new Image(item.getContent(), true));
            iv.setFitWidth(taille);
            iv.setFitHeight(taille);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            Rectangle masque = new Rectangle(taille, taille);
            masque.setArcWidth(30);
            masque.setArcHeight(30);
            itemContainer.setClip(masque);
            itemContainer.getChildren().add(iv);
        } else {
            Label lbl = new Label(item.getContent());
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
            lbl.setAlignment(Pos.CENTER);
            lbl.setWrapText(true);
            itemContainer.getChildren().add(lbl);
        }

        itemContainer.setOnDragDetected(event -> {
            Dragboard db = itemContainer.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getContent()); // On utilise le contenu comme ID unique
            db.setContent(content);
            event.consume();
        });

        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Supprimer Item");
        deleteItem.setOnAction(e -> {
            if (item.getTier() != null) item.getTier().removeItem(item);
            else currentTierList.removeUnrankedItem(item);
            refreshTiersGrid();
            refreshUnrankedArea();
        });
        menu.getItems().add(deleteItem);
        itemContainer.setOnContextMenuRequested(e -> menu.show(itemContainer, e.getScreenX(), e.getScreenY()));

        return itemContainer;
    }

    private Item findItemByContent(String content) {
        for (Item i : currentTierList.getUnrankedItems()) if (i.getContent().equals(content)) return i;
        for (Tier t : currentTierList.getTiers()) for (Item i : t.getItems()) if (i.getContent().equals(content)) return i;
        return null;
    }

    private Tier findTierByName(String name) {
        for (Tier t : currentTierList.getTiers()) if (t.getName().equals(name)) return t;
        return null;
    }

    private void handleAddItem() {
        if (!itemTextField.getText().isBlank() && currentTierList != null) {
            currentTierList.addUnrankedItem(new Item(itemTextField.getText().trim(), false));
            itemTextField.clear();
            refreshUnrankedArea();
        }
    }

    private void handleAddTier() {
        String name = tierNameField.getText();
        if (name != null && !name.isBlank() && currentTierList != null) {
            Tier t = new Tier(name.trim(), "#FF7F7F", currentTierList.getTiers().size());
            currentTierList.addTier(t);
            tierNameField.clear();
            refreshTiersGrid();
        }
    }

    private void handleAddMovieApi() {
        if (!itemTextField.getText().isBlank() && currentTierList != null) {
            String apiKey = DataManager.getInstance().getConfig().getTmdbApiKey();
            try {
                Item movieItem = TMDBApiManager.searchMovieAsItem(itemTextField.getText().trim(), apiKey);
                currentTierList.addUnrankedItem(movieItem);
                itemTextField.clear();
                refreshUnrankedArea();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur API", e.getMessage());
            }
        }
    }

    private void handleRenameTier(Tier tier) {
        TextInputDialog dialog = new TextInputDialog(tier.getName());
        dialog.setTitle("Renommer");
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                tier.setName(name);
                refreshTiersGrid();
            }
        });
    }

    private void handleColorTier(Tier tier) {
        TextInputDialog dialog = new TextInputDialog(tier.getColor());
        dialog.setTitle("Couleur");
        dialog.setHeaderText("Couleur hexadécimale (ex: #FF0000) :");
        dialog.showAndWait().ifPresent(color -> {
            if (color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                tier.setColor(color);
                refreshTiersGrid();
            }
        });
    }

    private void handleSave() {
        DataManager.getInstance().saveConfig();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Sauvegardé avec succès !");
    }

    private void handleGoHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage) previousButton.getScene().getWindow();
            stage.setScene(new Scene(root, 550, 700));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}