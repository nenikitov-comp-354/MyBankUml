package bank.controllers;

import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountChequingController extends AccountController {
    @FXML
    private Text feeText;

    public void setFee(BigDecimal n) {
        feeText.setText("Fee: " + n);
    }
}
