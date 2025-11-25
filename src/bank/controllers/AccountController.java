package bank.controllers;

import java.math.BigDecimal;

import bank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountController {
    @FXML
    private Text accountNameText;

    @FXML
    private Text amountText;

    protected SceneManager sceneManager = SceneManager.getInstance();

    public void setAccountNameText(String s) {
        accountNameText.setText(s);
    }

    public void setBalance(BigDecimal b) {
        amountText.setText(b.toString());
    }
}
