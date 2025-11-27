package bank.controllers;

import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountSavingsController extends AccountController {
    @FXML
    private Text interestRateText;

    public void setInterest(BigDecimal n) {
        interestRateText.setText("Interest Rate: " + n + " %");
    }
}
