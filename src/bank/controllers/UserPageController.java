package bank.controllers;

import bank.db.Account;
import bank.db.AccountChequing;
import bank.db.AccountCredit;
import bank.db.AccountSavings;
import bank.db.Customer;
import bank.util.SceneManager;
import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
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

    @FXML
    private Button transferFundsButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        try {
            Customer currentCustomer = sceneManager.getCustomer();
            userNameText.setText(
                "Welcome, " + currentCustomer.getFirstName() + "!"
            );
            List<Account> accounts = currentCustomer.getAccounts();

            for (Account a : accounts) {
                FXMLLoader loader = new FXMLLoader(
                    getClass()
                        .getResource(
                            "/fxml/" + a.getClass().getSimpleName() + ".fxml"
                        )
                );
                Parent card = loader.load();
                AccountController ac = loader.getController();
                ac.setAccountNameText(a.getName());
                ac.setBalance(a.getBalance());

                if (a instanceof AccountChequing) {
                    AccountChequing c = (AccountChequing) a;
                    AccountChequingController acc = (AccountChequingController) ac;
                    acc.setFee(c.getMonthlyFee());
                } else if (a instanceof AccountCredit) {
                    AccountCredit c = (AccountCredit) a;
                    AccountCreditController acc = (AccountCreditController) ac;
                    acc.setCreditLimitAndGrace(
                        c.getCreditLimit(),
                        c.getPaymentGraceDays()
                    );
                } else if (a instanceof AccountSavings) {
                    AccountSavings s = (AccountSavings) a;
                    AccountSavingsController asc = (AccountSavingsController) ac;
                    asc.setInterest(s.getInterestRate());
                } else {
                    throw new IllegalArgumentException(
                        "Account of another type\nAccount info: " + a
                    );
                }

                accountHolderVBox.getChildren().add(card);
                VBox.setMargin(card, new javafx.geometry.Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadTransferPage(ActionEvent event) {
        sceneManager.switchScene("/fxml/TransferPage.fxml", transferFundsButton);
    }
}
