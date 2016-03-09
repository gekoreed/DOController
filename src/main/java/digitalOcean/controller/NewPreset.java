package digitalOcean.controller;

import digitalOcean.dao.IOPerations;
import digitalOcean.entity.Preset;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by eshevchenko on 11.06.15 at 14:30.
 */
public class NewPreset {

    IOPerations dao = IOPerations.getInstance();

    @FXML
    TextField command;

    //description
    @FXML
    TextField description;


    public void saveNewPreset() {
        dao.saveNewPreset(new Preset(command.getText(), description.getText()));
        ((Stage) command.getScene().getWindow()).close();
    }
}
