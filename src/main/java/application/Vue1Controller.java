package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Vue1Controller {


    @FXML
    private VBox tierConfigPopup;

    static TierList tierlist;

    @FXML
    private TextField nomTier;
    
    @FXML
    private ColorPicker couleurTier;

    @FXML
    private GridPane grille;
    
    @FXML
    private VBox tierConfigPopup;

    @FXML
    private FlowPane tierS;

    @FXML
    private FlowPane tierA;

    @FXML
    private FlowPane tierB;

    @FXML
    private FlowPane tierC;

    @FXML
    private FlowPane tierD;

    @FXML
    private FlowPane tierE;

    @FXML
    private FlowPane tierF;

    @FXML
    private HBox reserveItems;

    @FXML
    private StackPane item1;

    @FXML
    private StackPane item2;

    @FXML
    private StackPane item3;

    @FXML
    private StackPane item4;

    private Node draggedItem;

    @FXML
    public void initialize() {

        makeDraggable(item1);
        makeDraggable(item2);
        makeDraggable(item3);
        makeDraggable(item4);

        setupDropZone(tierS);
        setupDropZone(tierA);
        setupDropZone(tierB);
        setupDropZone(tierC);
        setupDropZone(tierD);
        setupDropZone(tierE);
        setupDropZone(tierF);

        setupDropZone(reserveItems);
    }

    private void makeDraggable(Node item) {

        item.setOnDragDetected(event -> {

            draggedItem = item;

            Dragboard db = item.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("drag");

            db.setContent(content);

            event.consume();
        });
    }

    private void setupDropZone(Pane zone) {

        zone.setOnDragOver(event -> {

            if (event.getGestureSource() != zone
                    && event.getDragboard().hasString()) {

                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        zone.setOnDragDropped(event -> {

            if (draggedItem != null) {

                Parent oldParent = draggedItem.getParent();

                if (oldParent instanceof Pane oldPane) {
                    oldPane.getChildren().remove(draggedItem);
                }

                zone.getChildren().add(draggedItem);

                event.setDropCompleted(true);
            }

            event.consume();
        });
    }

    @FXML
    private void onTierClicked(MouseEvent event) {

        Node clickedTier = (Node) event.getSource();
        Node root = tierConfigPopup.getParent();

        Bounds tierBounds =
                clickedTier.localToScene(clickedTier.getBoundsInLocal());

        Bounds rootBounds =
                root.localToScene(root.getBoundsInLocal());

        double popupX =
                tierBounds.getMaxX() - rootBounds.getMinX() + 14;

        double popupY =
                tierBounds.getMinY() - rootBounds.getMinY() + 8;

        double maxPopupY =
                rootBounds.getHeight()
                        - tierConfigPopup.getPrefHeight()
                        - 8;

        tierConfigPopup.setTranslateX(popupX);
        tierConfigPopup.setTranslateY(
                Math.max(0, Math.min(popupY, maxPopupY))
        );

        tierConfigPopup.setVisible(true);
    }

    @FXML
    private void hideTierConfig() {
        tierConfigPopup.setVisible(false);
    }

    @FXML
    public void AddTier() {

        Color couleur = couleurTier.getValue();
        String hex = String.format("#%02X%02X%02X",
                (int)(couleur.getRed() * 255),
                (int)(couleur.getGreen() * 255),
                (int)(couleur.getBlue() * 255)
        );
        if (!tierlist.equals("") && tierlist != null){
           tierlist.addTier(new Tier(nomTier.getText(), hex,tierlist.getTiers().size() +1));
            StackPane stackpane = new StackPane();
            Label nom = new Label(nomTier.getText());
            stackpane.getChildren().add(nom);
            System.out.println("tier ajouté, le nom de la tierlist est" + tierlist.getName());
        }

    }

    @FXML
    public void envoyer(TierList tierList) {
        tierlist = tierList;
    }
    
    @FXML
    void retourPressEvent(MouseEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("vue2.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage =
                    (Stage) tierConfigPopup.getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {

            System.out.println("Erreur : " + e.getMessage());

        }
    }

    @FXML
    private void onMenuButtonClick() {

    }
}
