package digitalOcean.controller;

import digitalOcean.dao.IOPerations;
import digitalOcean.entity.Droplet;
import digitalOcean.services.DigitalOcean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Control {
    protected boolean isActivated = false;
    ObservableList<String> dropletObservableList =
            FXCollections.observableArrayList();
    ObservableList<String> presetsObservableList =
            FXCollections.observableArrayList();
    Map<String, Droplet> map = new HashMap<>();

    DigitalOcean de = DigitalOcean.getInstance();

    IOPerations dao = IOPerations.getInstance();

    Droplet droplet;
    @FXML
    public Label userNameLabel;
    @FXML
    Slider slider;
    @FXML
    CheckBox autoUpdate;
    @FXML
    public Label CPULoadFactor;
    @FXML
    public Label emailLabel;
    @FXML
    public Circle statusCircle;
    @FXML
    public Circle sshStatus;
    @FXML
    public Label label;
    @FXML
    public Label dropletStatus;
    @FXML
    public Label dropletName;
    @FXML
    public Label dropletIP;
    @FXML
    public ListView<String> dropletList;
    @FXML
    public ListView<String> presets;
    @FXML
    public TextField apiKeyText;
    @FXML
    public Label verifiedLabel;
    @FXML
    public TextArea loggerText;
    @FXML
    public TextArea console;
    @FXML
    public TextArea consolehidden;
    @FXML
    public TabPane tabPane;
    @FXML
    public TextField commandText;
    @FXML
    public PieChart chart;
    @FXML
    public AreaChart areachart;
    @FXML
    public AreaChart activitychart;
    @FXML
    public PieChart upTimeChart;
    @FXML
    public Button refreshButton;
    @FXML
    public Button copyButton;
    @FXML
    public Button copyButton2;
    @FXML
    ComboBox authTypeCombo;
    @FXML
    Label RSAKeyPath;
    @FXML
    PasswordField pwdField;
    @FXML
    TextField sshLogin;
    @FXML
    Button presetsButton;


    public void log(String log) {
        log(log, "");
    }

    public void log(String log, @SuppressWarnings("SameParameterValue") String comment) {
        loggerText.appendText(LocalTime.now().format(DateTimeFormatter.ISO_TIME).substring(0, 8)
                + "  " + log + "\n");
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                dao.saveOperation(log, comment);
                return null;
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }


    public void saveToFile() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File selectedDirectory = chooser.showDialog(dropletIP.getScene().getWindow());
        if (selectedDirectory == null) return;

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        File file = new File(selectedDirectory.getAbsolutePath() + "/DropletInfo" +
                String.format("_at_%s_%s_%s_%s", time.getHour(), time.getMinute(), date.getMonth(), date.getDayOfMonth()) + ".txt");
        FileOutputStream out = new FileOutputStream(file);

        StringBuilder str = new StringBuilder();
        for (Droplet d : de.getDroplets())
            str.append(d.toString()).append("\n------------------\n");
        out.write(str.toString().getBytes());
        out.close();
    }
}
