package digitalOcean.controller;

import digitalOcean.dao.IOPerations;
import digitalOcean.services.DigitalOcean;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

/**
 * Created by gekoreed on 3/5/16.
 */
public class CreateDropletController implements Initializable {

    DigitalOcean digitalOcean = DigitalOcean.getInstance();
    IOPerations ioPerations = IOPerations.getInstance();
    List<String> sshFingerprints;

    @FXML
    TextField newDropletName;

    @FXML
    TextField sskKeys;

    @FXML
    ComboBox region;

    @FXML
    ComboBox dropletSize;

    @FXML
    ComboBox dropletImage;

    @FXML
    CheckBox backup;

    @FXML
    CheckBox ip6;

    @FXML
    CheckBox saveSsh;

    @FXML
    Label error;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        error.setVisible(false);
        String[] regionNames = {"nyc1", "nyc2", "nyc3", "sfo1", "ams3", "ams2", "sgp1", "lon1", "fra1", "tor1"};
        region.setItems(FXCollections.observableList(asList(regionNames)));
        region.getSelectionModel().select(0);

        String[] drSizes = {"512mb", "1gb", "2gb", "4gb", "8gb", "16gb", "32gb"};
        dropletSize.setItems(FXCollections.observableList(asList(drSizes)));
        dropletSize.getSelectionModel().select(0);

        String[] drImages = {"ubuntu-14-04-x64", "ubuntu-14-04-x32", "ubuntu-15-10-x64", "ubuntu-15-10-x32",
                "ubuntu-12-04-x64", "ubuntu-12-04-x32"};
        dropletImage.setItems(FXCollections.observableList(asList(drImages)));
        dropletImage.getSelectionModel().select(0);

        backup.setSelected(false);
        ip6.setSelected(false);
        sshFingerprints = ioPerations.getSshFingerprints();
        for (int i = 0; i < sshFingerprints.size(); i++) {
            sskKeys.setText(sskKeys.getText() + sshFingerprints.get(i));
            if (i < sshFingerprints.size() - 1) {
                sskKeys.setText(sskKeys.getText() + ", ");
            }
        }
    }

    public void createDropletSubmit() {
        error.setVisible(false);
        String regionString = region.getSelectionModel().selectedItemProperty().getValue().toString();
        String sizeString = dropletSize.getSelectionModel().selectedItemProperty().getValue().toString();
        String imageString = dropletImage.getSelectionModel().selectedItemProperty().getValue().toString();

        String ssh = sskKeys.getText();

        boolean backups = backup.isSelected();
        boolean ipv6 = ip6.isSelected();

        sshFingerprints = new ArrayList<>(asList(sskKeys.getText().split(",")));
        sshFingerprints.forEach(String::trim);
        if (saveSsh.isSelected()) {
            ioPerations.serialize(sshFingerprints, "prints.ser");
        }

        boolean created = digitalOcean.createDroplet(newDropletName.getText(), regionString, sizeString, imageString, sshFingerprints, backups, ipv6);
        if (created)
            ((Stage) backup.getScene().getWindow()).close();
        else
            error.setVisible(true);

    }
}
