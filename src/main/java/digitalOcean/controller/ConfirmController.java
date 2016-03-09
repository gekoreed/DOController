package digitalOcean.controller;

import digitalOcean.config.APIKey;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by gekoreed on 5/9/15.
 */
public class ConfirmController extends Control {

    @FXML
    Button close;

    /**
     * Canceling the removing of droplet
     */
    public void noAction() {
        close();
    }

    private void close() {
        ((Stage) close.getScene().getWindow()).close();
    }

    /**
     * Submitting the droplet removing
     */
    public void yesAction() {
        boolean deleted = de.deleteDroplet(Long.valueOf(APIKey.DropletID));
        close();
    }
}
