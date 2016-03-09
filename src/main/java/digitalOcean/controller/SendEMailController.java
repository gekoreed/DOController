package digitalOcean.controller;

import digitalOcean.dao.Propertie;
import digitalOcean.entity.Droplet;
import digitalOcean.services.DigitalOcean;
import digitalOcean.services.EmailSender;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gekoreed on 5/3/15.
 */
public class SendEMailController implements Initializable {


    @FXML
    TextField email;
    @FXML
    Label validLabel;

    DigitalOcean digitalOcean = DigitalOcean.getInstance();

    String oldEmail;


    public void initialize(URL location, ResourceBundle resources) {
        String mail = Propertie.getPropertie("email");
        if (mail != null)
            email.setText(mail);
        oldEmail = email.getText();
    }

    public void sendEmail() {
        String mail = email.getText();
        if (validate(mail)) {
            if (!mail.equals(oldEmail)) {
                Propertie.addNewPropertie("email", mail);
            }
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    List<Droplet> droplets = digitalOcean.getDroplets();
                    StringBuilder stringBuilder = new StringBuilder();
                    droplets.forEach(droplet ->
                            stringBuilder.append(droplet.toString()).append("\n---------------------\n"));
                    new EmailSender().send(mail, stringBuilder.toString());
                    return null;
                }
            };
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
            Stage stage = (Stage) email.getScene().getWindow();
            stage.close();
        } else {
            validLabel.setVisible(true);
        }
    }

    public boolean validate(final String hex) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }
}
