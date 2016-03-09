package digitalOcean.dao;

import digitalOcean.entity.DropletStatus;
import digitalOcean.entity.Operation;
import digitalOcean.entity.Preset;
import digitalOcean.services.Platform;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by eshevchenko on 13.02.15 at 14:33.
 */
public class IOPerations {
    Map<String, Integer> operations = new LinkedHashMap<>();
    private static IOPerations instance;
    List<DropletStatus> dropletStatuses = new LinkedList<>();
    List<Preset> presets = new ArrayList<>();

    public static IOPerations getInstance() {
        if (instance != null)
            return instance;
        instance = new IOPerations();

        return instance;
    }

    public IOPerations() {
        operations = (Map<String, Integer>) deserialize("map.ser");
        if (operations == null)
            operations = new HashMap<>();
        presets = (List<Preset>) deserialize("presets.ser");
        if (presets == null)
            presets = new ArrayList<>();
        dropletStatuses = (List<DropletStatus>) deserialize("statuses.ser");
        if (dropletStatuses == null)
            dropletStatuses = new ArrayList<>();
    }

    /**
     * Saving operations like pressing button or
     * reloading something to MongoDB
     * Usually it is called thought log() method
     *
     * @param operation What has been done
     * @param comment   why?
     */
    public void saveOperation(String operation, String comment) {
        Operation op = new Operation();
        op.setComand(operation);
        op.setComment(comment);
        LocalDate date = LocalDate.now();
        op.setTime(date.getDayOfMonth() + "/" + date.getMonthValue());
        if (operations.keySet().contains(op.getTime())) {
            operations.put(op.getTime(), operations.get(op.getTime()) + 1);
        } else {
            operations.put(op.getTime(), 1);
        }
        serialize(operations, "map.ser");
    }


    /**
     * Getting map whitch shows how many operations
     * where done every day
     *
     * @return map of operations and their counts per day
     */
    public Map<String, Integer> getOperationsByDay() {
        return operations;
    }


    /**
     * Saving new Droplet status
     *
     * @param status dropletstatus
     */
    public void saveStatus(DropletStatus status) {
        dropletStatuses.add(status);
        serialize(dropletStatuses, "statuses.ser");
    }

    /**
     * Getting all statuses for spesial droplet
     *
     * @param id droplet ID
     * @return list of statuses
     */
    public List<DropletStatus> getDropletStatuses(Long id) {
        final Long searchableId = id == null ? 0 : id;
        List<DropletStatus> statusesList = dropletStatuses.stream().filter(s -> Objects.equals(id, s.getDropletID())).collect(Collectors.toList());
        if (statusesList.size() > 20) {
            dropletStatuses.removeAll(statusesList);
            statusesList = statusesList.subList(statusesList.size() - 20, statusesList.size() - 1);
            dropletStatuses.addAll(statusesList);
            serialize(dropletStatuses, "statuses.ser");
        }
        return statusesList;
    }


    public List<Preset> getPresets() {
        return presets;
    }

    public void saveNewPreset(Preset preset) {
        presets.add(preset);
        serialize(presets, "presets.ser");
    }

    public void deletePreset(String command) {
        Preset preset = null;
        for (Preset pt : presets) {
            if (pt.getCommand().equals(command))
                preset = pt;
        }
        presets.remove(preset);
        serialize(presets, "presets.ser");
    }


    public List<String> getSshFingerprints() {

        List<String> prints = (List<String>) deserialize("prints.ser");
        return prints == null ? Collections.EMPTY_LIST : prints;
    }


    public void serialize(Object toSer, String name) {
        try {
            FileOutputStream fileOut = new FileOutputStream(Platform.propertiesFolder + name);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(toSer);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    private Object deserialize(String name) {
        Object object = null;
        try {
            FileInputStream fileIn = new FileInputStream(Platform.propertiesFolder + name);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException ignored) {
            System.out.println("Serialization file was not found");
        }
        return object;
    }

}