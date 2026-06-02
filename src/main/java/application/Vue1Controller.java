package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.List;

public class Vue1Controller {

    static Tier tierSelectionne;
    @FXML
    TextField nvNom;

    @FXML
    private ImageView impor;

    @FXML
    private ImageView Maison;

    @FXML
    private ImageView sauv;

    @FXML
    TextField nvPlace;

    @FXML
    FlowPane unrakedItemZone;

    @FXML
    TextField nvHauteur;

    @FXML
    ImageView home;

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
    private Button addItem;


    @FXML
    private void onTierClicked(MouseEvent event) {

        Node clickedTier = (Node) event.getSource();
        tierSelectionne = (Tier) clickedTier.getUserData();
        nvCouleur.setValue(null);
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
    public void resetItem(){
        for (Tier tier : tierlist.getTiers()){
            List<Item> tousLesItems = tier.getItems();
            for (Item item : tousLesItems) {
                tierlist.addUnrankedItem(item);
                tier.removeItem(item);
            }
        }
        afficherItems();
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
            nomTier.setText(null);
        }

    }

    @FXML
    public void afficherUnrankedItems(){
        unrakedItemZone.getChildren().clear();
        List<Item> tousLesItems = tierlist.getUnrankedItems();
        for (Item item : tousLesItems) {
            System.out.println("item unranked ajouté :" + item);
            VBox vbox = new VBox();
            if(item.isImage()){
                ImageView image = new ImageView(item.getContent());
                image.setFitHeight(100);
                image.setFitWidth(100);
                vbox.getChildren().add(image);
            }
            else {
                Label label = new Label(item.getContent());
                label.setStyle("-fx-text-fill: white");
                label.setAlignment(Pos.CENTER);
                label.setPrefSize(100, 100);
                vbox.getChildren().add(label);
                vbox.setStyle("-fx-border-color: white; -fx-border-radius: 5");
            }
            unrakedItemZone.getChildren().add(vbox);

            vbox.setPrefSize( 100, 100);

        }

    }

    @FXML
    public void afficherTiers() {
        System.out.println("je suis sensé rafraichir les tiers");
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
    public void afficherItems() {
        for (Tier tier : tierlist.getTiers()){
            List<Item> tousLesItems = tier.getItems();
            tousLesItems.sort(Comparator.comparingInt(Item::getPlace));
            for (Item item : tousLesItems) {
                VBox vbox = new VBox();
                if(item.isImage()){
                    ImageView image = new ImageView(item.getContent());
                    vbox.getChildren().add(image);
                }
                else {
                    Label label = new Label(item.getContent());
                    vbox.getChildren().add(label);
                }

                vbox.setPrefSize( 100, 100);

            }   }
    }

    @FXML
    public void envoyer(TierList tierList) {
        tierlist = tierList;

        afficherTiers();
        afficherItems();
        afficherUnrankedItems();
    }

    @FXML
    private void modifierTier(Tier tier) {

        if(nvCouleur.getValue() != null){
            Color couleur = nvCouleur.getValue();
            String hex = String.format("#%02X%02X%02X",
                    (int)(couleur.getRed() * 255),
                    (int)(couleur.getGreen() * 255),
                    (int)(couleur.getBlue() * 255)
            );
            tier.setColor(hex);
        }

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
    public void supprimerTier(){
        tierlist.removeTier(tierSelectionne);
        hideTierConfig();
        nvPlace.clear();
        nvHauteur.clear();
        nvNom.clear();
        afficherTiers();
    }

    @FXML
    private void ouvrirAjoutItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue3.fxml"));
            Parent root = loader.load();

            Vue3Controller controller = loader.getController();
            controller.afficherTierList(tierlist);

            Scene scene = new Scene(root);
            Stage stage = (Stage) nomTier.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void handlehome_btn(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 550, 700);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void sauvegarderLocale() {
        if (tierlist != null) {
            DataManager.getInstance().enregistrerTiersList(tierlist);
        }
    }

    @FXML
    public void handleImporterBtn(MouseEvent event) {
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
}