package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import application.TierList;
import application.Tier;
import application.Item;
import java.io.IOException;
import java.util.List;

public class Vue2Controller {
    @FXML
    ImageView home;

    @FXML
    Button confirmer_btn;


    @FXML
    Label MesTierList;

    @FXML
    TextField nom;

    @FXML
    public void addTierList(String nomTL) {
        TierList tierList = new TierList(nomTL);
        DataManager.getInstance().addTierList(tierList);
        afficherList();
    }

    public void handleConfirmer_btn() {
        if(nom.getText() != null){
            addTierList(nom.getText());
        }

    }

    @FXML
    public void afficherList() {
        List<TierList> ToutesLesTierLists = DataManager.getInstance().getToutesLesTierLists();
        MesTierList.setText(ToutesLesTierLists.toString());
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

}
