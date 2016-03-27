package digitalOcean.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by gekoreed on 3/7/16.
 */
public class StageUtil {

    private ClassLoader classLoader = this.getClass().getClassLoader();

    private static StageUtil stageUtil;

    public static StageUtil getInstance() {
        if (stageUtil == null) {
            stageUtil = new StageUtil();
        }
        return stageUtil;
    }

    public void showStage(int height, int width, String xmlName, String windowName) throws IOException {
        showStage(height, width, xmlName, windowName, false);
    }

    public void showStage(int height, int width, String xmlName, String windowName, boolean resizable) throws IOException {
        Stage ps = new Stage();
        ps.setTitle(windowName);
        ps.setScene(getScene(height, width, xmlName));
        ps.setResizable(resizable);
        ps.show();
    }


    private Scene getScene(int height, int width, String xmlName) throws IOException {
        return new Scene(new FXMLLoader().load(classLoader.getResourceAsStream(xmlName)), width, height);
    }
}
