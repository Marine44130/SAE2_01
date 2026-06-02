package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;


import java.io.IOException;

public class Vue1Controller {

    static Tier tierSelectionne;
    @FXML
    TextField nvNom;

    @FXML
    TextField nvPlace;

    @FXML
    TextField nvHauteur;

    @FXML
    private ColorPicker nvCouleur;

    @FXML
    private Button validerModif;
    @FXML
    private Button suppTier;

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
    private void onTierClicked(MouseEvent event) {

        Node clickedTier = (Node) event.getSource();
        tierSelectionne = (Tier) clickedTier.getUserData();
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
        System.out.println("ancien tier = " + tierSelectionne);
        if(tierSelectionne != null ){
            modifierTier(tierSelectionne);
        }

    }

    @FXML
    public void AddTier() {

        Color couleur = couleurTier.getValue();
        String hex = String.format("#%02X%02X%02X",
                (int)(couleur.getRed() * 255),
                (int)(couleur.getGreen() * 255),
                (int)(couleur.getBlue() * 255)
        );
        if (!tierlist.equals("") && tierlist != null && !nomTier.equals("") ){
            tierlist.addTier(new Tier(nomTier.getText(), hex,tierlist.getTiers().size() +1));
            StackPane stackpane = new StackPane();
            Label nom = new Label(nomTier.getText());
            stackpane.getChildren().add(nom);
            System.out.println("tier ajouté, le nom de la tierlist est" + tierlist.getName());
            afficherTiers();
        }

    }

    @FXML
    public void afficherTiers() {
        grille.getChildren().clear();

        List<Tier> tousLesTier = tierlist.getTiers();
        tousLesTier.sort(Comparator.comparingInt(Tier::getPlace));


        for (Tier tier : tousLesTier) {
            StackPane stackpane = new StackPane();
            FlowPane flowPane = new FlowPane();
            Label label = new Label(tier.getName());

            stackpane.setPrefSize( 100, tier.getHauteur());
            stackpane.setStyle("-fx-background-color:"+ tier.getColor() +";");
            stackpane.getChildren().add(label);
            stackpane.setOnMouseClicked(this::onTierClicked);
            stackpane.setUserData(tier);
            stackpane.setOnMouseClicked(this::onTierClicked);
            grille.addRow(tousLesTier.indexOf(tier), stackpane, flowPane);

        }
    }

    @FXML
    public void envoyer(TierList tierList) {
        tierlist = tierList;
        afficherTiers();
    }

    @FXML
    private void modifierTier(Tier tier) {
        Color couleur = nvCouleur.getValue();
        String hex = String.format("#%02X%02X%02X",
                (int)(couleur.getRed() * 255),
                (int)(couleur.getGreen() * 255),
                (int)(couleur.getBlue() * 255)
        );
        tier.setColor(hex);

        if (!nvNom.getText().isEmpty()) {
            tier.setName(nvNom.getText());
            System.out.println("nom");
        }

        if (!nvPlace.getText().isEmpty()) {
            tier.setPlace(Integer.parseInt(nvPlace.getText())); }



        if (!nvHauteur.getText().isEmpty()) {
            tier.setHauteur(Integer.parseInt(nvHauteur.getText())); }


        nvPlace.clear();
        nvHauteur.clear();
        nvNom.clear();
        System.out.println("nv tier = " + tier);
        afficherTiers();
    }

    @FXML
    public void supprimerTier(){
        tierlist.removeTier(tierSelectionne);
        hideTierConfig();
        nvPlace.clear();
        nvHauteur.clear();
        nvNom.clear();
        afficherTiers();
    }

    public void afficherTierList(TierList tl) {
        this.tierlist = tl;
    }
}
