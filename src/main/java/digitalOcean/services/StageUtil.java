package digitalOcean.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by gekoreed on 3/7/16.
 */
public class StageUtil {

    ClassLoader classLoader = this.getClass().getClassLoader();

    private static StageUtil stageUtil;

    public static StageUtil getInstance() {
        if (stageUtil == null) {
            stageUtil = new StageUtil();
        }
        return stageUtil;
    }

    public void showStage(int height, int width, String xmLname, String windowName) throws IOException {
        showStage(height, width, xmLname, windowName, false);
    }

    public void showStage(int height, int width, String xmLname, String windowName, boolean resizable) throws IOException {
        Stage ps = new Stage();
        ps.setTitle(windowName);
        ps.setScene(getScene(height, width, xmLname));
        ps.setResizable(resizable);
        ps.show();
    }


    private Scene getScene(int height, int width, String XMLname) throws IOException {
        return new Scene(new FXMLLoader().load(classLoader.getResourceAsStream(XMLname)), width, height);
    }
}
