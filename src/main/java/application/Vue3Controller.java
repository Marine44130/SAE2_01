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
    private String nextTierColor = "#FF7F7F";

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
    @FXML private ImageView shareIcon;
    @FXML private ImageView saveIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
            FlowPane itemsRow = new FlowPane();
            itemsRow.setHgap(5);
            itemsRow.setVgap(5);
            itemsRow.setPadding(new Insets(5));
            itemsRow.setStyle("-fx-background-color: #3a3a3a; -fx-border-color: #1e1e1e;");

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

            Label lblTier = new Label(tier.getName());
            lblTier.setAlignment(Pos.CENTER);
            lblTier.setStyle("-fx-background-color: " + tier.getColor() + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: #1e1e1e;");

            lblTier.prefHeightProperty().bind(itemsRow.heightProperty());
            lblTier.minHeightProperty().bind(itemsRow.heightProperty());
            lblTier.setMaxWidth(Double.MAX_VALUE);

            ContextMenu tierMenu = new ContextMenu();
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
            tierMenu.getItems().add(deleteTier);
            lblTier.setContextMenu(tierMenu);

            VBox controlsRow = new VBox(2);
            controlsRow.setAlignment(Pos.CENTER);
            controlsRow.setStyle("-fx-background-color: #242424; -fx-border-color: #1e1e1e;");
            controlsRow.prefHeightProperty().bind(itemsRow.heightProperty());
            controlsRow.minHeightProperty().bind(itemsRow.heightProperty());

            Button upBtn = new Button("▲");
            Button downBtn = new Button("▼");
            upBtn.setStyle("-fx-background-color: #454545; -fx-text-fill: white; -fx-font-size: 10;");
            downBtn.setStyle("-fx-background-color: #454545; -fx-text-fill: white; -fx-font-size: 10;");

            upBtn.setOnAction(e -> {
                int index = currentTierList.getTiers().indexOf(tier);
                if (index > 0) {
                    tier.setPlace(index - 1);
                    currentTierList.getTiers().get(index - 1).setPlace(index);
                    currentTierList.tri();
                    refreshTiersGrid();
                }
            });
            downBtn.setOnAction(e -> {
                int index = currentTierList.getTiers().indexOf(tier);
                if (index < currentTierList.getTiers().size() - 1) {
                    tier.setPlace(index + 1);
                    currentTierList.getTiers().get(index + 1).setPlace(index);
                    currentTierList.tri();
                    refreshTiersGrid();
                }
            });
            controlsRow.getChildren().addAll(upBtn, downBtn);

            tiersColumn1.getChildren().add(lblTier);
            tiersColumn2.getChildren().add(itemsRow);
            tiersColumn3.getChildren().add(controlsRow);
        }
    }

    private void refreshUnrankedArea() {
        unrankedArea.getChildren().clear();
        if (currentTierList == null) return;

        FlowPane unrankedContainer = new FlowPane();
        unrankedContainer.setHgap(8);
        unrankedContainer.setVgap(8);
        unrankedContainer.setPadding(new Insets(10));

        unrankedContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != unrankedContainer && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        unrankedContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
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
        StackPane itemContainer = new StackPane();
        itemContainer.setPrefSize(75, 75);
        itemContainer.setStyle("-fx-background-color: #616161; -fx-background-radius: 8; -fx-border-color: #888888; -fx-border-radius: 8;");

        if (item.isImage()) {
            ImageView iv = new ImageView(new Image(item.getContent(), true));
            iv.setFitWidth(75);
            iv.setFitHeight(75);
            iv.setPreserveRatio(true);

            Rectangle clip = new Rectangle(75, 75);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            itemContainer.setClip(clip);
            itemContainer.getChildren().add(iv);
        } else {
            Label lbl = new Label(item.getContent());
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11;");
            lbl.setAlignment(Pos.CENTER);
            lbl.setWrapText(true);
            itemContainer.getChildren().add(lbl);
        }

        itemContainer.setOnDragDetected(event -> {
            Dragboard db = itemContainer.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getContent());
            db.setContent(content);
            event.consume();
        });

        return itemContainer;
    }

    private Item findItemByContent(String content) {
        for (Item i : currentTierList.getUnrankedItems()) if (i.getContent().equals(content)) return i;
        for (Tier t : currentTierList.getTiers()) for (Item i : t.getItems()) if (i.getContent().equals(content)) return i;
        return null;
    }

    @FXML
    private void handleAddTier() {
        String name = tierNameField.getText();
        if (name != null && !name.isBlank() && currentTierList != null) {
            Tier t = new Tier(name.trim(), nextTierColor, currentTierList.getTiers().size());
            currentTierList.addTier(t);
            tierNameField.clear();
            refreshTiersGrid();
        }
    }

    @FXML
    private void handleColorAction() {
        TextInputDialog dialog = new TextInputDialog(nextTierColor);
        dialog.setTitle("Couleur du prochain Tier");
        dialog.setHeaderText("Entrez une couleur (ex: #00FF00, #336699) :");
        dialog.showAndWait().ifPresent(color -> {
            if (color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                nextTierColor = color;
            } else {
                showAlert(Alert.AlertType.WARNING, "Format Incorrect", "La couleur doit être au format Hexadécimal.");
            }
        });
    }

    @FXML
    private void handleAddItem() {
        if (!itemTextField.getText().isBlank() && currentTierList != null) {
            currentTierList.addUnrankedItem(new Item(itemTextField.getText().trim(), false));
            itemTextField.clear();
            refreshUnrankedArea();
        }
    }


    @FXML
    private void handleAddImage() {
        // En attente d'implémentation (si vous voulez ajouter depuis le PC plus tard)
    }

    @FXML
    private void handlePrevious() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage) previousButton.getScene().getWindow();
            stage.setScene(new Scene(root, 550, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFinish() {
        DataManager.getInstance().saveConfig();
        showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Changements appliqués avec succès.");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}