package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Vue1Controller {
    @FXML
    private VBox tierConfigPopup;

    @FXML
    private void onTierClicked(MouseEvent event) {
        Node clickedTier = (Node) event.getSource();
        Node root = tierConfigPopup.getParent();

        Bounds tierBounds = clickedTier.localToScene(clickedTier.getBoundsInLocal());
        Bounds rootBounds = root.localToScene(root.getBoundsInLocal());

        double popupX = tierBounds.getMaxX() - rootBounds.getMinX() + 14;
        double popupY = tierBounds.getMinY() - rootBounds.getMinY() + 8;
        double maxPopupY = rootBounds.getHeight() - tierConfigPopup.getPrefHeight() - 8;

        tierConfigPopup.setTranslateX(popupX);
        tierConfigPopup.setTranslateY(Math.max(0, Math.min(popupY, maxPopupY)));
        tierConfigPopup.setVisible(true);
    }

    @FXML
    private void hideTierConfig() {
        tierConfigPopup.setVisible(false);
    }

    @FXML
    void retourPressEvent(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vue2.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tierConfigPopup.getScene().getWindow();
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
