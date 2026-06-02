package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static application.Vue1Controller.tierlist;

public class Vue2Controller {
    @FXML
    ImageView home;

    @FXML
    private ImageView Maison;

    @FXML
    SplitMenuButton supprimer_menu;

    @FXML
    SplitMenuButton dupliquer_menu;

    @FXML
    Button confirmer_btn;

    @FXML
    HBox MesTierList;

    @FXML
    TextField nom;

    @FXML
    public void addTierList(String nomTL) {
        List<TierList> toutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();

        for (TierList tierList : toutesLesTierLists) {
            if (tierList.getName().equals(nomTL)) {
                return;
            }
        }
        TierList tierList = new TierList(nomTL);
        DataManager.getInstance().addTierList(tierList);
        afficherListDansMesList();
    }

    @FXML
    public void handleConfirmer_btn() {
        if (nom.getText() != null && !nom.getText().equals("")) {
            addTierList(nom.getText());
        }
    }

    @FXML
    public void afficherListDansMesList() {
        supprimer_menu.getItems().clear();
        afficherListSupprimables();
        afficherListDupliquables();
        MesTierList.getChildren().clear();

        List<TierList> toutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();

        for (TierList tierList : toutesLesTierLists) {
            VBox ligne = new VBox(10);
            ligne.setAlignment(Pos.CENTER);
            ligne.setStyle("-fx-padding: 8; -fx-background-color:  #525252; -fx-border-radius: 5; -fx-background-radius: 5; ");

            Label nomLabel = new Label(tierList.getName());
            nomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill : white ;");
            nomLabel.setPrefWidth(150);
            nomLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            nomLabel.setAlignment(Pos.CENTER);

            Button ouvrirBtn = new Button("Ouvrir");
            ouvrirBtn.setStyle("-fx-background-color: #5865F2; -fx-text-fill : white ; -fx-background-radius: 5; -fx-border-color: #525252; -fx-border-radius: 5");
            ouvrirBtn.setOnAction(e -> ouvrirTierList(tierList));

            ligne.getChildren().addAll(nomLabel, ouvrirBtn);
            MesTierList.getChildren().add(ligne);
        }
    }

    @FXML
    public void afficherListSupprimables() {
        supprimer_menu.getItems().clear();

        List<TierList> toutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();

        for (TierList tierList : toutesLesTierLists) {
            MenuItem nomTL = new MenuItem(tierList.getName());
            nomTL.setOnAction(e -> supprimerTierList(tierList));
            supprimer_menu.getItems().add(nomTL);
        }
    }

    @FXML
    public void afficherListDupliquables() {
        dupliquer_menu.getItems().clear();

        List<TierList> toutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();

        for (TierList tierList : toutesLesTierLists) {
            MenuItem nomTL = new MenuItem(tierList.getName());
            nomTL.setOnAction(e -> dupliquerTierList(tierList));
            dupliquer_menu.getItems().add(nomTL);
        }
    }

    @FXML
    private void supprimerTierList(TierList tierList) {
        DataManager.getInstance().removeTierList(tierList);
        afficherListDansMesList();
    }

    @FXML
    private void dupliquerTierList(TierList tierList) {
        DataManager.getInstance().addTierList(tierList);
        afficherListDansMesList();
    }


    private void ouvrirTierList(TierList tierList) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue3.fxml"));
            Parent root = loader.load();

            Vue3Controller controller = loader.getController();
            controller.afficherTierList(tierList);
            System.out.println(tierList);

            Scene scene = new Scene(root);
            Stage stage = (Stage) MesTierList.getScene().getWindow();
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
}