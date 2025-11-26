package bank.controllers;

import bank.db.Account;
import bank.db.operation.OperationLock;
import bank.util.SceneManager;
import java.math.BigDecimal;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import lombok.Setter;

public class AccountController {
    @FXML
    private Text accountNameText;

    @FXML
    private Text amountText;

    @FXML
    private Button lockCardButton;

    @FXML
    private GridPane cardGridPane;

    private Account account;

    protected SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {}

    public void initializeData(Account account) {
        this.account = account;
        setLockStyle(account.isLocked());
    }

    public void setAccountNameText(String s) {
        accountNameText.setText(s);
    }

    public void setBalanceText(BigDecimal b) {
        amountText.setText(b.toString());
    }

    private void setLockStyle(boolean isLocked) {
        if (isLocked) {
            lockCardButton.setText("Unlock Card");
            cardGridPane.setStyle("-fx-background-color: #c5c5c5;");
            for (Node n : cardGridPane.getChildren()) {
                if (n instanceof Text)
                    n.setStyle("-fx-fill: #4e4e4e");

            }
        } else {
            lockCardButton.setText("Lock Card");
            cardGridPane.setStyle("-fx-background-color: #f2f2f2;");
            for (Node n : cardGridPane.getChildren()) {
                if (n instanceof Text)
                    if (n.getId().equals("cardTypeName"))
                        n.setStyle("-fx-fill: #338aee");
                    else
                        n.setStyle("-fx-fill: black");
            }
        }
    }

    @FXML
    private void lockCard(ActionEvent event) {
        try {
            OperationLock ol;
            if (!account.isLocked()) {
                ol = new OperationLock(account, true);
                setLockStyle(true);
            }
            else {
                ol = new OperationLock(account, false);
                setLockStyle(false);
            }

            sceneManager.getDb().addOperation(ol);
            sceneManager.getDb().processOperations();

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }
}
