package digitalOcean.entity;

/**
 * Created by gekoreed on 3/7/16.
 */
public class LoadStat {

    public String load;
    public String freeMemory;

    public LoadStat(String load, String freeMemory) {
        this.load = load;
        this.freeMemory = freeMemory;
    }
}
