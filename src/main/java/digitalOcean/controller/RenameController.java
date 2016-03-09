package digitalOcean.controller;

import digitalOcean.config.APIKey;
import digitalOcean.services.DigitalOcean;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gekoreed on 11/15/14.
 */
public class RenameController implements Initializable {
    @FXML
    private Label error;
    @FXML
    public TextField newName;

    DigitalOcean d = DigitalOcean.getInstance();

    public void initialize(URL location, ResourceBundle resources) {
        error.setVisible(false);
    }

    public void applyNewName() throws IOException {
        try {
            int responce = d.renameDroplet(newName.getText(), APIKey.DropletID);
            if (responce == 201 || responce == 200) {
                Stage s = (Stage) newName.getScene().getWindow();
                s.close();
            } else {
                error.setText("Something wrong!");
                error.setVisible(true);
            }
        } catch (SocketException exception) {
            error.setText("Network is unavailable!");
            error.setVisible(true);
        }
    }
}
