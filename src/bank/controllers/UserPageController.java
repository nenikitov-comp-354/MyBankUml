package bank.controllers;

import java.io.IOException;
import java.util.List;

import bank.db.Account;
import bank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class UserPageController {

    @FXML
    private VBox accountHolderVBox;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        try {
            List<Account> accounts = sceneManager.getCustomer().getAccounts();

            for (Account a : accounts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + a.getClass().getSimpleName() + ".fxml"));
                Parent card = loader.load();
                accountHolderVBox.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
