package bank.controllers;

import java.io.IOException;
import java.sql.SQLException;

import bank.db.Account;
import bank.db.TransactionInfo;
import bank.db.operation.OperationTransaction;
import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

public class TransferDetailsPageController {
    @FXML
    private Button transferButton;

    @FXML
    private Button confirmTransferButton;

    @FXML
    private Button cancelTransferButton;

    @FXML
    private Text transferFromText;

    @FXML
    private Text transferToText;

    @FXML
    private Text bankNameText;

    @FXML
    private Text amountText;

    private TransactionInfo currentTransaction;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
    }

    public void initializeData(TransactionInfo currentTransaction) {
        this.currentTransaction = currentTransaction;

        transferFromText.setText(currentTransaction.getSource().getName());
        transferToText.setText(currentTransaction.getDestination().getName());
        bankNameText.setText(sceneManager.getCustomer().getBranch().getBank().getName());
        amountText.setText(currentTransaction.getAmount().toString());
    }

    @FXML
    private void handleConfirmTransfer(ActionEvent event) {
        try {
            sceneManager.getDb().addOperation(new OperationTransaction(currentTransaction));
            sceneManager.getDb().processOperations();

            FXMLLoader loader = new FXMLLoader(
                    getClass()
                        .getResource(
                            "/fxml/TransactionConfirmationPage.fxml"
                        )
                );

            Scene newScene;
            try {
                newScene = new Scene(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Stage confirmStage = new Stage();
            confirmStage.initOwner(confirmTransferButton.getScene().getWindow());
            confirmStage.initModality(Modality.APPLICATION_MODAL);
            confirmStage.setScene(newScene);
            confirmStage.showAndWait();

            sceneManager.switchScene("/fxml/UserPage.fxml", confirmTransferButton);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelTransfer(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/TransferPage.fxml",
            cancelTransferButton
        );
    }
}
