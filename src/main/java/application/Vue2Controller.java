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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static application.Vue1Controller.tierlist;

public class Vue2Controller {

    @FXML
    private ImageView home;

    @FXML
    private ImageView impor;

    @FXML
    private ImageView Maison;

    @FXML
    private ImageView sauv;

    @FXML
    SplitMenuButton supprimer_menu;

    @FXML
    private SplitMenuButton exporter_menu;

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
        afficherListExportables();

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
    private void dupliquerTierList(TierList tierListOrigine) {
        if (tierListOrigine == null) return;

        TierList copieTierList = clonerTierList(tierListOrigine);

        if (copieTierList != null) {
            copieTierList.setName(tierListOrigine.getName() + " - Copie");
            DataManager.getInstance().addTierList(copieTierList);
            DataManager.getInstance().saveConfig();
            afficherListDansMesList();
        }
    }

    private TierList clonerTierList(TierList source) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(source);

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            return (TierList) ois.readObject();
        } catch (Exception e) {
            System.err.println("Erreur lors de la duplication de la TierList : " + e.getMessage());
            return null;
        }
    }


    private void ouvrirTierList(TierList tierList) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue1.fxml"));
            Parent root = loader.load();
            Vue1Controller controller = loader.getController();
            if (tierList != null) {
                controller.envoyer(tierList);
                DataManager.getInstance().saveConfig();
            }
            Scene scene = new Scene(root, 1293, 952);
            Stage stage = (Stage) confirmer_btn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de redirection : " + e.getMessage());
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

    @FXML
    public void afficherListExportables() {
        exporter_menu.getItems().clear();

        List<TierList> toutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();

        for (TierList tierList : toutesLesTierLists) {
            MenuItem nomTL = new MenuItem(tierList.getName());
            nomTL.setOnAction(e -> exporterTierList(tierList));
            exporter_menu.getItems().add(nomTL);
        }
    }

    private void exporterTierList(TierList tierList) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la Tier-List : " + tierList.getName());

        fileChooser.setInitialFileName(tierList.getName() + ".ser");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Tier-List (*.ser)", "*.ser"));

        Stage stage = (Stage) confirmer_btn.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(file))) {
                oos.writeObject(tierList);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation réussie");
                alert.setHeaderText(null);
                alert.setContentText("La Tier-List \"" + tierList.getName() + "\" a bien été exportée !");
                alert.showAndWait();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'exportation");
                alert.setContentText("Impossible d'exporter la Tier-List : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}