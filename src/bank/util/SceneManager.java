package bank.util;

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
    private static final SceneManager instance = new SceneManager();

    /**
     * Returns the shared SceneManager instance
     */
    public static SceneManager getInstance() { return instance; }

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
        }
    }
}
