package digitalOcean.controller;

import digitalOcean.config.APIKey;
import digitalOcean.dao.IOPerations;
import digitalOcean.dao.Propertie;
import digitalOcean.services.DigitalOcean;
import digitalOcean.services.StageUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class NewController implements Initializable {
    @FXML
    public TextField userName;
    @FXML
    public PasswordField newPwd;
    @FXML
    public PasswordField pwd;
    @FXML
    public TextField newUserName;
    @FXML
    public Label error;
    @FXML
    public TextField newApiKey1;
    @FXML
    public Button okButton;
    @FXML
    public Label used;
    @FXML
    public Rectangle rectangle;

    StageUtil stageUtil = StageUtil.getInstance();
    DigitalOcean digitalOcean = DigitalOcean.getInstance();
    IOPerations dao = IOPerations.getInstance();

    private FXMLLoader loader;
    private boolean api = false;
    private boolean user = false;
    private boolean pass = false;

    public void initialize(URL location, ResourceBundle resources) {
        error.setVisible(false);
        loader = new FXMLLoader();
    }


    public void savingNewApiKey() throws IOException {
        APIKey.APIKey = "Bearer " + newApiKey1.getText();
        Propertie.addNewPropertie("api", APIKey.APIKey);
        Propertie.updateLoginInPropertieFile(System.getProperty("user.name"));
        stageUtil.showStage(150, 300, "fxml/authTypeChoise.fxml", "Auth Type");
        ((Stage) newApiKey1.getScene().getWindow()).close();
    }

    private boolean registration() {
        return userName.getText().equals("");
    }


    public void onEnter() throws IOException {
        if (!okButton.isDisabled())
            savingNewApiKey();
    }


    public void instructions() throws IOException {
        stageUtil.showStage(200, 400, "fxml/instructions.fxml", "Instructions");
    }


    public void clearExisting() {
        error.setVisible(true);
        if (api && user && pass)
            okButton.setDisable(false);
        else
            okButton.setDisable(true);

    }

    public void clearNew() {
        newPwd.setText("");
        newUserName.setText("");
        newApiKey1.setText("");
        okButton.setDisable(false);
        rectangle.setVisible(false);
        used.setVisible(false);
        error.setVisible(false);
        api = false;
        user = false;
        pass = false;
        error.setText("");
        used.setText("");
    }


    private void dissableForm() {
        used.setTextFill(RED);
        okButton.setDisable(true);
        user = false;
    }

    public void checkAPI() throws IOException {
        error.setVisible(true);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                return digitalOcean.checkProperKey("Bearer " + newApiKey1.getText());
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            if ((Boolean) task.getValue()) {
                error.setTextFill(GREEN);
                if (user && pass)
                    okButton.setDisable(false);
                else
                    okButton.setDisable(true);
                error.setText("API key is OK");
                api = true;
                okButton.setDisable(false);
            } else {
                error.setTextFill(RED);
                error.setText("wrong API key");
                okButton.setDisable(true);
                api = false;
                okButton.setDisable(true);
            }
            task.cancel();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    public void checkPasswordLenth() {
        String password = newPwd.getText();
        if (password.equals("")) {
            pass = false;
            okButton.setDisable(true);
            return;
        } else pass = true;
        if (user && api)
            okButton.setDisable(false);
        rectangle.setVisible(true);
        int lenght = password.length();
        if (lenght < 3) rectangle.setFill(RED);
        if (lenght >= 3 && lenght < 5) rectangle.setFill(PINK);
        if (lenght >= 5 && lenght < 7) rectangle.setFill(LIGHTGREEN);
        if (lenght >= 7) rectangle.setFill(GREEN);
    }
}
