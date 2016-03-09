package digitalOcean.entity;

import java.io.Serializable;

/**
 * Created by eshevchenko on 11.06.15 at 14:08.
 */

public class Preset implements Serializable {

    private String id;
    private String userid;

    private String command;
    private String description;

    public Preset() {
    }

    public Preset(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
