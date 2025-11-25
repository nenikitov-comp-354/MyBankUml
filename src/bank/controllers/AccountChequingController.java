package bank.controllers;

import java.math.BigDecimal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class AccountChequingController extends AccountController {
    @FXML
    private Text feeText;

    @FXML
    private Button sendMoneyButton;

    public void setFee(BigDecimal n) {
        feeText.setText("Fee: " + n);
    }

    public void loadSendMoney(ActionEvent event) {
        sceneManager.switchScene("/fxml/SendMoneySavingsPage.fxml", sendMoneyButton);
    }
}
