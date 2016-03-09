package digitalOcean.entity;

/**
 * Created by eshevchenko on 14.02.15 at 14:47.
 */
public class Operation {

    private String id;
    private String accountID;
    private String time;
    private String comand;
    private String comment;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComand() {
        return comand;
    }

    public void setComand(String comand) {
        this.comand = comand;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
