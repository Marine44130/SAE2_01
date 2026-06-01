package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Vue3Controller implements Initializable {

    private TierList currentTierList;

    @FXML
    private VBox unrankedArea;
    @FXML
    private TextField itemTextField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button addImageButton;
    @FXML
    private VBox tiersColumn1;
    @FXML
    private VBox tiersColumn2;
    @FXML
    private VBox tiersColumn3;
    @FXML
    private TextField tierNameField;
    @FXML
    private Button addTierButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button finishButton;
    @FXML
    private Button addMovieApiButton;

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
            Label lblTier = new Label(tier.getName());
            lblTier.setPrefHeight(tier.getHauteur());
            lblTier.setPrefWidth(70);
            lblTier.setAlignment(Pos.CENTER);
            lblTier.setStyle("-fx-background-color: " + tier.getColor() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #1e1e1e;");

            ContextMenu menu = new ContextMenu();
            MenuItem renameItem = new MenuItem("Renommer");
            renameItem.setOnAction(e -> handleRenameTier(tier));
            MenuItem deleteItem = new MenuItem("Supprimer");
            deleteItem.setOnAction(e -> {
                currentTierList.removeTier(tier);
                refreshTiersGrid();
            });
            menu.getItems().addAll(renameItem, deleteItem);
            lblTier.setContextMenu(menu);
            tiersColumn1.getChildren().add(lblTier);

            HBox itemsRow = new HBox(10);
            itemsRow.setPrefHeight(tier.getHauteur());
            itemsRow.setStyle("-fx-background-color: #3a3a3a; -fx-border-color: #1e1e1e; -fx-padding: 5;");
            itemsRow.setAlignment(Pos.CENTER_LEFT);

            itemsRow.setOnDragOver(event -> {
                if (event.getGestureSource() != itemsRow && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            itemsRow.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Item dragItem = findItemInTierList(db.getString());
                    if (dragItem != null) {
                        if (dragItem.getTier() != null) {
                            dragItem.getTier().removeItem(dragItem);
                        } else {
                            currentTierList.removeUnrankedItem(dragItem);
                        }
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

            HBox controlBox = new HBox(2);
            controlBox.setPrefHeight(tier.getHauteur());
            controlBox.setAlignment(Pos.CENTER);
            Button up = new Button("▲");
            Button down = new Button("▼");
            up.setStyle("-fx-font-size: 9;");
            down.setStyle("-fx-font-size: 9;");

            up.setOnAction(e -> {
                int index = currentTierList.getTiers().indexOf(tier);
                if (index > 0) {
                    tier.setPlace(index - 1);
                    currentTierList.getTiers().get(index - 1).setPlace(index);
                    currentTierList.tri();
                    refreshTiersGrid();
                }
            });
            controlBox.getChildren().addAll(up, down);
            tiersColumn3.getChildren().add(controlBox);
        }
    }

    private void refreshUnrankedArea() {
        unrankedArea.getChildren().clear();
        if (currentTierList == null) return;

        HBox container = new HBox(10);
        container.setStyle("-fx-padding: 10;");
        container.setOnDragOver(event -> {
            if (event.getGestureSource() != container && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        container.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Item dragItem = findItemInTierList(db.getString());
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
            container.getChildren().add(createItemNode(item));
        }
        unrankedArea.getChildren().add(container);
    }

    private Node createItemNode(Item item) {
        Node node;
        if (item.isImage()) {
            ImageView iv = new ImageView(new Image(item.getContent(), true));
            iv.setFitWidth(50);
            iv.setFitHeight(50);
            iv.setPreserveRatio(true);
            node = iv;
        } else {
            Label lbl = new Label(item.getContent());
            lbl.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 5;");
            node = lbl;
        }

        node.setOnDragDetected(event -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getContent());
            db.setContent(content);
            event.consume();
        });

        return node;
    }

    private Item findItemInTierList(String content) {
        for (Item item : currentTierList.getUnrankedItems()) {
            if (item.getContent().equals(content)) return item;
        }
        for (Tier t : currentTierList.getTiers()) {
            for (Item item : t.getItems()) {
                if (item.getContent().equals(content)) return item;
            }
        }
        return null;
    }

    private void handleAddItem() {
        String txt = itemTextField.getText();
        if (txt != null && !txt.isBlank() && currentTierList != null) {
            currentTierList.addUnrankedItem(new Item(txt.trim(), false));
            itemTextField.clear();
            refreshUnrankedArea();
        }
    }

    private void handleAddTier() {
        String name = tierNameField.getText();
        if (name != null && !name.isBlank() && currentTierList != null) {
            Tier t = new Tier(name.trim(), "#555555", currentTierList.getTiers().size());
            currentTierList.addTier(t);
            tierNameField.clear();
            refreshTiersGrid();
        }
    }

    private void handleAddMovieApi() {
        String title = itemTextField.getText();
        if (title != null && !title.isBlank() && currentTierList != null) {
            String apiKey = DataManager.getInstance().getConfig().getTmdbApiKey();
            try {
                Item movieItem = TMDBApiManager.searchMovieAsItem(title.trim(), apiKey);
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
        dialog.setHeaderText("Nouveau nom :");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                tier.setName(name);
                refreshTiersGrid();
            }
        });
    }

    private void handleSave() {
        showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Changements appliqués avec succès.");
    }

    private void handleGoHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage) previousButton.getScene().getWindow();
            stage.setScene(new Scene(root, 550, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}