package digitalOcean.controller;

import digitalOcean.config.APIKey;
import digitalOcean.dao.Propertie;
import digitalOcean.services.Coor;
import digitalOcean.services.StageUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gekoreed on 5/3/15.
 */
public class AuthChooserController implements Initializable {

    @FXML
    CheckBox rsa;

    @FXML
    CheckBox pass;

    Stage ps;
    StageUtil stageUtil = StageUtil.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void apply() throws IOException {
        ps = (Stage) rsa.getScene().getWindow();
        if (pass.isSelected()) {
            Propertie.addNewPropertie("authType", "pas");
            Propertie.addNewPropertie("keyPath", "pas");
            APIKey.keyPath = "unused";
            showNewWindow();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select your id RSA file");
            File file = fileChooser.showOpenDialog(ps);
            if (file != null) {
                Propertie.addNewPropertie("authType", "rsa");
                String path = file.getAbsolutePath();
                Propertie.addNewPropertie("keyPath", path);
                APIKey.keyPath = path;
                showNewWindow();
            }
        }
    }


    private void showNewWindow() throws IOException {
        stageUtil.showStage(Coor.height, Coor.width, "fxml/sample.fxml", "Your DigitalOcean", true);
    }


    public void onClickedRSA() {
        pass.setSelected(false);
    }


    public void onClickedPassword() {
        rsa.setSelected(false);
    }
}
