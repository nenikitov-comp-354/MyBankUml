package bank.util;

import bank.db.BankDb;
import bank.db.Customer;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility singleton for loading an FXML file and replacing the current scene on the given node's stage
 */
public class SceneManager {
    private BankDb db;
    private Customer c;
    private static final SceneManager instance = new SceneManager();

    public void setDb(BankDb db) {
        this.db = db;
    }

    public BankDb getDb() {
        return db;
    }

    public void setCustomer(Customer c) {
        this.c = c;
    }

    public Customer getCustomer() {
        return c;
    }

    /**
     * Returns the shared SceneManager instance
     */
    public static SceneManager getInstance() {
        return instance;
    }

    private SceneManager() {}

    /**
     * Switches to a new scene loaded from the given FXML path
     *
     * @param fxml - Path to the FXML file
     * @param sourceNode - Any UI node currently in the active scene
     */
    public void switchScene(String fxml, Node sourceNode) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println(fxml + " was not found");
            e.printStackTrace();
        }
    }
}
