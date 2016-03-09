package digitalOcean.controller;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gekoreed on 3/12/15.
 */
public class ErrorController extends Control implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void closeWindow() {
        Platform.exit();
    }
}
