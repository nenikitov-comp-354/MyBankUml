package bank.controllers;

import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TransferDetailsPageController {
    @FXML
    private Button transferButton;

    @FXML
    private Button confirmTransferButton;

    @FXML
    private Button cancelTransferButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void handleConfirmTransfer(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/TransactionConfirmationPage.fxml",
            confirmTransferButton
        );
    }

    @FXML
    private void handleCancelTransfer(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/TransferPage.fxml",
            cancelTransferButton
        );
    }
}
