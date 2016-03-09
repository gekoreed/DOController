package digitalOcean.entity;

import java.io.Serializable;

/**
 * Created by gekoreed on 2/21/15.
 */
public class DropletStatus implements Serializable {
    private String id;
    private Long dropletID;
    private String memory;
    private String CPUUsage;
    private String activeMem;
    private String inactiveMem;
    private String swap;

    public DropletStatus(Long dropletID, String memory, String CPUUsage, String activeMem, String inactiveMem, String swap) {
        this.dropletID = dropletID;
        this.memory = memory;
        this.CPUUsage = CPUUsage;
        this.activeMem = activeMem;
        this.inactiveMem = inactiveMem;
        this.swap = swap;
    }

    public DropletStatus() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDropletID() {
        return dropletID;
    }

    public void setDropletID(Long dropletID) {
        this.dropletID = dropletID;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getCPUUsage() {
        return CPUUsage;
    }

    public void setCPUUsage(String CPUUsage) {
        this.CPUUsage = CPUUsage;
    }

    public String getActiveMem() {
        return activeMem;
    }

    public void setActiveMem(String activeMem) {
        this.activeMem = activeMem;
    }

    public String getInactiveMem() {
        return inactiveMem;
    }

    public void setInactiveMem(String inactiveMem) {
        this.inactiveMem = inactiveMem;
    }

    public String getSwap() {
        return swap;
    }

    public void setSwap(String swap) {
        this.swap = swap;
    }
}
