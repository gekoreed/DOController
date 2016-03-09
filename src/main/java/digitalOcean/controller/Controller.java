package digitalOcean.controller;

import digitalOcean.config.APIKey;
import digitalOcean.dao.IOPerations;
import digitalOcean.dao.Propertie;
import digitalOcean.entity.Droplet;
import digitalOcean.entity.DropletStatus;
import digitalOcean.entity.LoadStat;
import digitalOcean.entity.Preset;
import digitalOcean.services.DigitalOcean;
import digitalOcean.services.StageUtil;
import digitalOcean.ssh.Command;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalTime.now;
import static java.util.stream.Collectors.toList;

public class Controller extends Control implements Initializable {

    IOPerations ioPerations = IOPerations.getInstance();
    Command command = Command.getInstance();
    StageUtil stageUtil = StageUtil.getInstance();
    @FXML
    public Label memLabel;
    Timeline updater;
    DigitalOcean digitalOcean = DigitalOcean.getInstance();
    private String namesList;
    static List<Preset> preset;
    InputStream presetsInput;

    private double autoUpdateRate = 10;

    /**
     * Setting all necesary info on program startup
     *
     * @param location  location
     * @param resources recources
     */
    public void initialize(URL location, ResourceBundle resources) {
        APIKey.keyPath = Propertie.getPropertie("keyPath");
        apiKeyText.setText(APIKey.APIKey);
        invalidate();
        setListeners();
        upTimeChart();
        addActivityChart();
        String authType = Propertie.getPropertie("authType");
        authTypeCombo.setItems(FXCollections.observableList(Arrays.asList("by RSA", "by password")));
        if (authType.contains("rsa"))
            authTypeCombo.getSelectionModel().select(0);
        else
            authTypeCombo.getSelectionModel().select(1);
        APIKey.usingPassword = authType.contains("pas");
        if (!APIKey.usingPassword)
            pwdField.setVisible(false);
        RSAKeyPath.setText("RSA key path: " + APIKey.keyPath);
        dropletList.getSelectionModel().selectFirst();
        userNameLabel.setText(Propertie.getLogin());
        autoUpdateRate = Double.valueOf(Propertie.getPropertie("interval"));
        loadPresets();
        setAutoUpdate();
    }

    private void loadPresets() {
        if (preset == null)
            preset = ioPerations.getPresets();
        presetsObservableList.clear();
        presetsObservableList.addAll(preset.stream().map(pr -> pr.getCommand() + " - " + pr.getDescription()).collect(toList()));
        presets.setItems(presetsObservableList);
    }

    /**
     * Activity chart
     * Showing how often user was doing
     * something in the program
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void addActivityChart() {
        log("Showing activiti chart");
        activitychart.setTitle("Activity chart");
        XYChart.Series series = new XYChart.Series();
        series.setName("activity");
        int i = 0;
        Set<Map.Entry<String, Integer>> entries = ioPerations.getOperationsByDay().entrySet();
        int j = entries.size() - 10;
        for (Map.Entry<String, Integer> entry : entries) {
            if (i >= j)
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            i++;
        }
        activitychart.getData().clear();
        activitychart.getData().add(series);

    }

    @FXML
    private void updateRSAKeyPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New RSA file");
        File file = fileChooser.showOpenDialog(upTimeChart.getScene().getWindow());
        if (file != null) {
            String path = file.getAbsolutePath();
            Propertie.addNewPropertie("keyPath", path);
            APIKey.keyPath = path;
            RSAKeyPath.setText("RSA key path: " + path);
        }
    }

    /**
     * Adding the up time chart
     */
    private void upTimeChart() {
        upTimeChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Up", 98),
                new PieChart.Data("Down", 2)));
        upTimeChart.setTitle("Up Time");
    }

    /**
     * Adding the memory usage chart
     */
    @SuppressWarnings("unchecked")
    private void addMemoryChart() {
        areachart.setTitle("Memory Usage");
        XYChart.Series series = new XYChart.Series();
        series.setName("Mem Load");

        List<DropletStatus> dropletStatuses = ioPerations.getDropletStatuses(droplet.getDropletID());

        int i = 0;
        for (DropletStatus status : dropletStatuses) {
            series.getData().add(new XYChart.Data(String.valueOf(i++), Integer.valueOf(status.getActiveMem()) / 1000));
        }

        areachart.getData().clear();
        areachart.getData().add(series);
    }

    /**
     * Autoupdate timer
     * to refresh data automatically
     */
    public void setAutoUpdate() {
        slider.setValue(autoUpdateRate);
        if (updater != null)
            updater.stop();
        updater = new Timeline(new KeyFrame(Duration.seconds(autoUpdateRate), event -> invalidateOnAutoUpdate()));
        updater.setCycleCount(Timeline.INDEFINITE);
        if (Propertie.getPropertie("autoUpdate").toLowerCase().contains("y")) {
            autoUpdate.setSelected(true);
            updater.play();
            isActivated = true;
        } else {
            autoUpdate.setSelected(false);
        }
    }

    /**
     * Getting the memory usage from the server
     * and saving it to the database
     */
    private LoadStat setStatistics(String login, String pwd) {
        consolehidden.setText("");
        if (APIKey.selectedIP.equals(""))
            APIKey.selectedIP = "127.0.0.1";

        String execute = command.runCommand("cat /proc/meminfo; cat /proc/loadavg",
                login, APIKey.selectedIP, pwd);
        if (execute == null)   // Connection not succesful
            return null;

        String[] splitted = execute.split("\n");

        String memory = extractInt(splitted[1]);

        DropletStatus status = new DropletStatus();
        status.setDropletID(droplet.getDropletID());
        status.setMemory(memory);
        status.setActiveMem(extractInt(splitted[5]));
        status.setInactiveMem(extractInt(splitted[6]));
        status.setSwap(extractInt(splitted[4]));
        status.setCPUUsage("0");
        ioPerations.saveStatus(status);

        int length = splitted.length;
        return new LoadStat(splitted[length - 1].split(" ")[0], memory);
    }

    public static String extractInt(String str) {
        Matcher matcher = Pattern.compile("\\d+").matcher(str);
        if (!matcher.find())
            throw new NumberFormatException("For input string [" + str + "]");
        return matcher.group();
    }

    /**
     * Rebot the server controller button
     */
    public void rebootTheServer() {
        command.disconnect();
        new Thread(() -> log(digitalOcean.rebootDroplet(APIKey.DropletID)))
                .run();
    }

    /**
     * Refresh button controller
     */
    public void refreshData() {
        requestMemoryChart();
        invalidate();
        log("Refreshed!");
    }

    /**
     * Method for resfeshing data on screen.
     * It gets newest data from the server
     * and sets it to labels
     */
    public void invalidate() {
        Task task = new Task() {
            protected List<Droplet> call() throws Exception {
                return digitalOcean.getDroplets();
            }
        };
        task.setOnSucceeded(data -> {
            List<Droplet> droplets = (List<Droplet>) task.getValue();
            if (droplets != null) {
                List<String> names = new ArrayList<>();
                for (Droplet droplet : droplets) {
                    dropletStatus.setText("Droplet Status: " + droplet.getStatus());
                    dropletName.setText("Name: " + droplet.getDropletName());
                    dropletIP.setText("Droplet IP: " + droplet.getIPAddress());
                    names.add(droplet.getDropletName());
                    if (!dropletObservableList.contains(droplet.getDropletName())) // if there is not droplet with the  same name
                        dropletObservableList.addAll(droplet.getDropletName());  //  add the droplet to list
                    dropletList.setItems(dropletObservableList);
                    map.put(droplet.getDropletName(), droplet);
                    statusCircle.setFill(droplet.isActive() ? Color.GREEN : Color.RED);
                }
                List<String> toRemove = new ArrayList<>();
                for (String exDroplet : dropletObservableList) {
                    if (!names.contains(exDroplet))
                        toRemove.add(exDroplet);
                }
                dropletObservableList.removeAll(toRemove);
            }
            dropletObservableList.remove(namesList);
            label.setText("Time: " + now().toString().substring(0, now().toString().length() - 4));
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * Updating interface on timer
     */
    public void invalidateOnAutoUpdate() {
        Thread thread = new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                for (Droplet droplet : digitalOcean.getDroplets()) {
                    map.put(droplet.getDropletName(), droplet);
                }
                return null;
            }
        });
        thread.setDaemon(true);
        thread.start();
        Droplet droplet = map.get(dropletList.getSelectionModel().selectedItemProperty().getValue());
        if (droplet != null) {
            APIKey.selectedIP = droplet == null ? "" : droplet.getIPAddress();
            dropletStatus.setText("Droplet Status: " + droplet.getStatus());
            dropletName.setText("Name: " + droplet.getDropletName());
            dropletIP.setText("Droplet IP: " + droplet.getIPAddress());
            if (droplet.isActive())
                statusCircle.setFill(Color.GREEN);
            else
                statusCircle.setFill(Color.RED);
            label.setText("Time: " + now().toString().substring(0, now().toString().length() - 4));
        }
        requestMemoryChart();
    }

    /**
     * Setting the listener to what to
     * do when the droplet is changed
     */
    public void setListeners() {
        areachart.setOnMouseClicked(mouseEvent -> requestMemoryChart());
        slider.valueProperty().addListener((ov, old_val, new_val) -> changeTimerInterval(new_val.doubleValue()));
        authTypeCombo.setOnAction(actionEvent -> {
            command.disconnect();
            if (authTypeCombo.getSelectionModel().selectedItemProperty().getValue().toString().contains("RSA")) {
                Propertie.addNewPropertie("authType", "rsa");
                APIKey.usingPassword = false;
                pwdField.setVisible(false);
            } else {
                Propertie.addNewPropertie("authType", "pas");
                APIKey.usingPassword = true;
                pwdField.setVisible(true);
            }
        });
        presets.getSelectionModel().selectedItemProperty().addListener((observableValue, s, name) -> {
            if (name != null) {
                String com = name.split(" - ")[0];
                commandText.setText(com);
                presets.setVisible(false);
                presetsButton.setText("Presets");
            }
        });
        dropletList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, name) -> {
            areachart.getData().clear();
            upTimeChart.getData().clear();
            droplet = map.get(name);
            console.setText("");
            APIKey.selectedIP = droplet.getIPAddress();
            dropletStatus.setText("Droplet Status: "
                    + droplet.getStatus());
            dropletName.setText("Name: " + droplet.getDropletName());
            dropletIP.setText("Droplet IP: " + droplet.getIPAddress());
            if (droplet.isActive())
                statusCircle.setFill(Color.GREEN);
            else
                statusCircle.setFill(Color.RED);
            log("Droplet changed to: " + droplet.getDropletName());
            APIKey.DropletID = droplet.getDropletID().toString();
            addMemoryChart();
            upTimeChart();
            Thread t = new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    command.reconnect(sshLogin.getText(), APIKey.selectedIP, pwdField.getText());
                    return null;
                }
            });
            t.setDaemon(true);
            t.start();
        });
    }

    /**
     * Powering selected droplet of
     */
    public void powerOffButtonPressed() {
        command.disconnect();
        new Thread(() -> log(digitalOcean.powerOff(APIKey.DropletID))).run();
    }

    /**
     * Powering selected droplet on
     */
    public void powerONButtonPressed() {
        new Thread(() -> log(digitalOcean.powerOn(APIKey.DropletID))).run();
    }

    /**
     * Saving new API key
     *
     * @throws IOException exception in reading the propertie file
     */
    public void saveNewApiKey() throws IOException {
        if (digitalOcean.checkProperKey(apiKeyText.getText())) {
            Propertie.updateKey(apiKeyText.getText());
            log("ApiKey changed!");
        } else
            log("APIKey was not changed. Something wrong with it!");
    }

    /**
     * Copy selected droplet IP address
     */
    public void copyIP() {
        copyToClipboard(dropletIP, 12);
    }

    /**
     * Copy selected droplet name
     */
    public void copyName() {
        copyToClipboard(dropletName, 6);
    }

    /**
     * Copying text to the Clipboard
     *
     * @param label label to be copied
     * @param pos   starting position
     */
    public void copyToClipboard(Label label, int pos) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(label.getText().substring(pos)), null);
            log(label.getText().substring(pos) + " copied ");
        } catch (StringIndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Renaming selected droplet
     * It also renames that droplet on digitalOcean web interface
     */
    public void renameDroplet() throws IOException {
        String dName = dropletName.getText().replace("Name: ", "");
        System.out.println(dName);
        if (!map.keySet().contains(dName))
            return;
        APIKey.DropletID = String.valueOf(map.get(dName).getDropletID());
        namesList = dName;

        stageUtil.showStage(150, 300, "fxml/renameDialod.fxml", "Rename Droplet");
    }

    /**
     * Running typed comman over SSH on the selected droplet
     */
    public void runCommand() {
        //cat /proc/loadavg  cat /proc/meminfo
        presets.setVisible(false);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                return command.runCommand(commandText.getText(), sshLogin.getText(), APIKey.selectedIP, pwdField.getText());
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            String value = (String) task.getValue();
            if (value == null) {
                sshStatus.setFill(Color.RED);
            } else {
                sshStatus.setFill(Color.web("#15a412"));
                console.setText(value + "\n" + console.getText());
            }
            task.cancel();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void logOut() throws IOException {
        stageUtil.showStage(300, 500, "fxml/dialog.fxml", "Login");
        ((Stage) tabPane.getScene().getWindow()).close();
    }


    /**
     * Binding for setStatictica button
     */
    public void statistica() {
        Thread thread = new Thread(new UpdateTask());
        thread.setDaemon(true);
        thread.start();
    }

    public void requestMemoryChart() {
        Task task = new UpdateTask();
        task.setOnSucceeded(workerStateEvent -> {
            LoadStat loadStat = (LoadStat) task.getValue();
            if (loadStat == null) {
                sshStatus.setFill(Color.RED);
                return;
            }
            sshStatus.setFill(Color.web("#15a412"));
            String load = loadStat.load;
            CPULoadFactor.setText("Load: " + load);
            if (Double.valueOf(load) < 0.5)
                CPULoadFactor.setTextFill(Color.GREEN);
            else
                CPULoadFactor.setTextFill(Color.RED);
            memLabel.setText("Free: " + new BigDecimal(loadStat.freeMemory).divide(new BigDecimal(1000), 0)
                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " mb.");
            addMemoryChart();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void closeApp() {
        Platform.exit();
    }

    public void deleteDroplet() throws IOException {
        stageUtil.showStage(160, 310, "fxml/confirm.fxml", "Confirm Deleting");
    }

    public void createDroplet() throws IOException {
        stageUtil.showStage(380, 410, "fxml/newDroplet.fxml", "Create new Droplet");
    }

    public void SendEmailReport() throws IOException {
        stageUtil.showStage(150, 350, "fxml/email.fxml", "EMail sender");
    }

    /**
     * Creating the terminal preset for User
     */
    public void newPreset() throws IOException {
        preset = null;
        stageUtil.showStage(200, 400, "fxml/presets.fxml", "New Preset");
    }

    /**
     * Showing the list of Users terminal presets
     * on "Show presets" button pressed
     */
    public void showPresetsForTerminal() {
        loadPresets();
        if (presets.isVisible()) {
            presets.setVisible(false);
            presetsButton.setText("Presets");
        } else {
            presets.setVisible(true);
            presetsButton.setText("Hide presets");
        }
    }

    /**
     * Removing the selected terminal preset from User
     * database
     */
    public void deletePreset() {
        preset = null;
        dao.deletePreset(commandText.getText());
        loadPresets();
    }

    public void autoUpdateAction() {
        if (autoUpdate.isSelected()) {
            updater.play();
            Propertie.autoUpdate(true);
        } else {
            updater.stop();
            Propertie.autoUpdate(false);
        }
    }

    public void changeTimerInterval(double newVal) {
        autoUpdateRate = newVal;
        Propertie.addNewPropertie("interval", String.valueOf(newVal));
        setAutoUpdate();
    }

    public void cancelSSH() {
        command.disconnect();
    }

    class UpdateTask extends Task {
        @Override
        protected LoadStat call() throws Exception {
            return setStatistics(sshLogin.getText(), pwdField.getText());
        }
    }
}