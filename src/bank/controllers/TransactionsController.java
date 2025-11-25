package bank.controllers;

import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TransactionsController {
    @FXML
    private Button savingsButton;

    @FXML
    private Button contactsButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button cancelButtonContact;

    @FXML
    private Button cancelButtonSavings;

    @FXML
    private Button transferButton;

    @FXML
    private Button confirmTransferButton;

    @FXML
    private Button cancelTransferButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void handleSavings(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/SendMoneySavingsPage.fxml",
            savingsButton
        );
    }

    @FXML
    private void handleContacts(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/SendMoneyContacts.fxml",
            contactsButton
        );
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        sceneManager.switchScene("/fxml/AccountChequing.fxml", cancelButton);
    }

    @FXML
    private void handleCancelContact(ActionEvent event) {
        sceneManager.switchScene("/fxml/ChequingSendMoney.fxml", cancelButtonContact);
    }

    @FXML
    private void handleCancelSavings(ActionEvent event) {
        sceneManager.switchScene("/fxml/ChequingSendMoney.fxml", cancelButtonSavings);
    }
    @FXML
    private void handleTransferDetails(ActionEvent event) {
        sceneManager.switchScene("/fxml/TransferDetailsPage.fxml", transferButton);
    }

    @FXML
    private void handleConfirmTransfer(ActionEvent event) {
        sceneManager.switchScene("/fxml/TransactionConfirmationPage.fxml", confirmTransferButton);
    }
    @FXML
    private void handleCancelTransfer(ActionEvent event) {
        sceneManager.switchScene("/fxml/ChequingSendMoney.fxml", cancelTransferButton);
    }
}
