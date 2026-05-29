package org.example.sae2_01;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class HelloController {
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
    private void onMenuButtonClick() {

    }
}
