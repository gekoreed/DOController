package digitalOcean.entity;


import java.util.Objects;

@SuppressWarnings("ALL")
public class Droplet {
    private String DropletName;
    private Long DropletID;
    private String status;
    private String OS;
    private String IPAddress;
    private String gateway;
    private String kernelID;
    private String kernelName;
    private String kernelVersion;
    private String created;

    public Droplet() {
    }

    public Droplet(Builder builder) {
        this.kernelVersion = builder.kernelVersion;
        this.DropletName = builder.DropletName;
        this.DropletID = builder.DropletID;
        this.status = builder.status;
        this.OS = builder.OS;
        this.IPAddress = builder.IPAddress;
        this.gateway = builder.gateway;
        this.kernelID = builder.kernelID;
        this.kernelName = builder.kernelName;
        this.created = builder.created;
    }


    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isActive() {
        return this.getStatus().toLowerCase().equals("active");
    }

    public String getDropletName() {
        return DropletName;
    }

    public void setDropletName(String dropletName) {
        DropletName = dropletName;
    }

    public Long getDropletID() {
        return DropletID;
    }

    public void setDropletID(Long dropletID) {
        DropletID = dropletID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getKernelID() {
        return kernelID;
    }

    public void setKernelID(String kernelID) {
        this.kernelID = kernelID;
    }

    public String getKernelName() {
        return kernelName;
    }

    public void setKernelName(String kernelName) {
        this.kernelName = kernelName;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    @Override
    public String toString() {
        return String.format("Droplet name: %s\n" +
                        "Droplet ID: %d\n" +
                        "STATUS: %s\n" +
                        "Operating System: %s\n" +
                        "  IP address: %s\n" +
                        "  Gateway: %s\n" +
                        "Kernel:\n" +
                        "  Kernel ID: %s\n" +
                        "  Kernel name: %s\n" +
                        "  Kernel version: %s",
                this.getDropletName(), this.getDropletID(), this.getStatus(), this.getOS(), this.getIPAddress(),
                this.getGateway(), this.getKernelID(), this.getKernelName(), this.getKernelVersion());
    }

    @Override
    public boolean equals(Object n) {
        return Objects.equals(this.DropletID, ((Droplet) n).DropletID);
    }

    public static class Builder {
        private String DropletName;
        private Long DropletID;
        private String status;
        private String OS;
        private String IPAddress;
        private String gateway;
        private String kernelID;
        private String kernelName;
        private String kernelVersion;
        private String created;

        public Builder setCreated(String created2) {
            created = created2;
            return this;
        }

        public Builder dropletName(Object name) {
            DropletName = (String) name;
            return this;
        }

        public Builder dropletID(Object name) {
            DropletID = Long.valueOf(name.toString());
            return this;
        }

        public Builder status(Object name) {
            status = (String) name;
            return this;
        }

        public Builder OS(Object name) {
            OS = (String) name;
            return this;
        }

        public Builder IPAddress(Object name) {
            IPAddress = (String) name;
            return this;
        }

        public Builder gateway(Object name) {
            gateway = (String) name;
            return this;
        }

        public Builder kernelID(Object name) {
            kernelID = name.toString();
            return this;
        }

        public Builder kernelName(Object name) {
            kernelName = (String) name;
            return this;
        }

        public Builder kernelVersion(Object name) {
            kernelVersion = (String) name;
            return this;
        }

        public Droplet build() {
            return new Droplet(this);
        }
    }
}
