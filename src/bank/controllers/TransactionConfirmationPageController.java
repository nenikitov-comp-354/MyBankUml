package bank.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TransactionConfirmationPageController {
    @FXML
    private Button doneButton;

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) doneButton.getScene().getWindow();
        stage.close();
    }
}
