package bank.controllers;

import java.math.BigDecimal;

import bank.db.Account;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountController {

    @FXML
    private Text accountNameText;

    @FXML
    private Text feeText;

    @FXML
    private Text interestRateText;

    @FXML
    private Text creditLimitText;

    @FXML
    private Text gracePeriodText;


    public void setAccountNameText(String s) {
        accountNameText.setText(s);
    }

    public void setFeeForChecking(BigDecimal n) {
        feeText.setText("Fee: " + n);
    }

    public void setCreditLimitAndGraceForCredit(BigDecimal limit, int grace) {
        creditLimitText.setText("Credit Limit: " + limit);
        gracePeriodText.setText("Grace period: " + grace);
    }

    public void setInterestForSavings(BigDecimal n) {
        interestRateText.setText("Interest Rate: " + n + " %");
    }

    public void lockCard(Account a) {

    }
}
