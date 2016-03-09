package digitalOcean;

import digitalOcean.config.APIKey;
import digitalOcean.dao.Propertie;
import digitalOcean.services.Coor;
import digitalOcean.services.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class App extends Application {
    public Label dropletStatus = new Label("Status: ");
    boolean mainWindow = false;
    Stage ps;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ps = primaryStage;
        primaryStage.setOnCloseRequest(App::closeApp);
        awaitLoading();

        Platform.setPath();

        if (Propertie.getLogin().equals("new")) {
            showNewWindow("fxml/dialog.fxml");
        } else {
            APIKey.APIKey = Propertie.getApiKeyFromConfig();
            mainWindow = true;
            if (checkPaths())
                showNewWindow("fxml/sample.fxml");
        }

    }


    private void awaitLoading() throws IOException {
        StackPane root = new StackPane();
        ps.setTitle("Loading");
        ps.setScene(new Scene(root, 200, 200));
        ps.show();
    }

    private void showNewWindow(String xml) {
        showNewWindow(xml, 500, 300);
    }

    private void showNewWindow(String xml, int width, int height) {
        try {
            Parent root = new FXMLLoader().load(this.getClass().getClassLoader().getResourceAsStream(xml));

            ps.setTitle("Your digitalOcean");
            if (mainWindow)
                ps.setScene(new Scene(root, Coor.width, Coor.height));
            else
                ps.setScene(new Scene(root, width, height));
            ps.setResizable(false);
            ps.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPaths() {
        String keyPath = Propertie.getPropertie("keyPath");
        if (keyPath == null) {
            mainWindow = false;
            showNewWindow("fxml/authTypeChoise.fxml", 300, 150);
            return false;
        } else
            APIKey.keyPath = keyPath;
        return true;
    }

    public static void closeApp(WindowEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
