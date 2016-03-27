package digitalOcean;

import digitalOcean.config.APIKey;
import digitalOcean.dao.Propertie;
import digitalOcean.services.Platform;
import digitalOcean.services.StageUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class App extends Application {

    private StageUtil stageUtil = StageUtil.getInstance();
    private Stage ps;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ps = primaryStage;
        primaryStage.setOnCloseRequest(App::closeApp);

        Platform.setPath();

        if (Propertie.getLogin().equals("new")) {
            showNewWindow("fxml/dialog.fxml", 500, 300);
        } else {
            APIKey.APIKey = Propertie.getApiKeyFromConfig();
            if (hasRSAKeyPath())
                showNewWindow("fxml/sample.fxml", 867, 723);
        }

    }


    private void showNewWindow(String xml, int width, int height) {
        try {
            stageUtil.showStage(height, width, xml, "Your digitalOcean", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasRSAKeyPath() {
        String keyPath = Propertie.getPropertie("keyPath");
        if (keyPath == null) {
            showNewWindow("fxml/authTypeChoise.fxml", 300, 150);
            return false;
        } else
            APIKey.keyPath = keyPath;
        return true;
    }

    private static void closeApp(WindowEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
