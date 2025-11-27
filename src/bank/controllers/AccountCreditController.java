package bank.controllers;

import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountCreditController extends AccountController {
    @FXML
    private Text creditLimitText;

    @FXML
    private Text gracePeriodText;

    public void setCreditLimitAndGrace(BigDecimal limit, int grace) {
        creditLimitText.setText("Credit Limit: " + limit);
        gracePeriodText.setText("Grace period: " + grace);
    }
}
