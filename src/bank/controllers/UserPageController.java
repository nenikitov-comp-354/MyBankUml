package bank.controllers;

import java.io.IOException;
import java.util.List;

import bank.db.Account;
import bank.db.Customer;
import bank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UserPageController {

    @FXML
    private VBox accountHolderVBox;
    @FXML
    private Text userNameText;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        try {
            Customer currentCustomer = sceneManager.getCustomer();
            userNameText.setText("Welcome, " + currentCustomer.getFirstName() + "!");
            List<Account> accounts = currentCustomer.getAccounts();

            for (Account a : accounts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + a.getClass().getSimpleName() + ".fxml"));
                Parent card = loader.load();
                accountHolderVBox.getChildren().add(card);
                VBox.setMargin(card, new javafx.geometry.Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
