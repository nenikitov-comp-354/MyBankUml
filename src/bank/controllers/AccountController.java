package bank.controllers;

import bank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AccountController {
    @FXML
    private Text accountNameText;

    protected SceneManager sceneManager = SceneManager.getInstance();

    public void setAccountNameText(String s) {
        accountNameText.setText(s);
    }
}
