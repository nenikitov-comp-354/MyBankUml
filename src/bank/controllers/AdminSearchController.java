package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AdminSearchController {
    @FXML
    private VBox customersHolderVBox;

    @FXML
    private Button cancelSearchButton;

    @FXML
    private javafx.scene.control.TextField searchInput;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        Customer loggedIn = sceneManager.getCustomer();

        if (loggedIn == null || !loggedIn.isAdmin()) {
            customersHolderVBox.getChildren().clear();

            // hide search bar since they can't search admins
            searchInput.setVisible(false);
            searchInput.setManaged(false);

            // Also disable search bar since they shouldn't search customers
            searchInput.setDisable(true);

            Label message = new Label(
                "You must be an admin to access this page."
            );
            message.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");

            customersHolderVBox.getChildren().add(message);

            return;
        } else {
            customersHolderVBox.setVisible(true);
            customersHolderVBox.setManaged(true);

            // show search bar
            searchInput.setVisible(true);
            searchInput.setManaged(true);
            // enable search bar
            searchInput.setDisable(false);

            Map<Integer, Customer> customersMap = sceneManager
                .getDb()
                .getCustomers();
            loadCustomers(customersMap.values().stream().toList());
        }
    }

    private void loadCustomers(List<Customer> customers) {
        customersHolderVBox.getChildren().clear();
        for (Customer c : customers) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CustomerCard.fxml")
                );
                Parent card = loader.load();
                CustomerCardController cc = loader.getController();
                cc.setCustomer(c);
                customersHolderVBox.getChildren().add(card);
                VBox.setMargin(card, new javafx.geometry.Insets(15));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSearch() {
        Customer loggedIn = sceneManager.getCustomer();

        if (loggedIn == null || !loggedIn.isAdmin()) {
            return;
        }

        String query = searchInput.getText().trim().toLowerCase();
        try {
            List<Customer> results = sceneManager
                .getDb()
                .getCustomersSearch(new String[] { query });
            loadCustomers(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadUserPage(ActionEvent event) {
        sceneManager.switchScene("/fxml/UserPage.fxml", cancelSearchButton);
    }
}
