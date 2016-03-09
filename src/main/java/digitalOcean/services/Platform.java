package digitalOcean.services;

import java.io.File;

/**
 * Created by gekoreed on 3/3/16.
 */
public class Platform {

    public static OSType osType;
    public static String propertiesFolder;


    public static void setPath() {
        osType = OSType.valueOf(System.getProperty("os.name").substring(0, 3).toUpperCase());
        propertiesFolder = osType.getPropertyFolder();
        createDirectory(propertiesFolder);
    }

    public static void createDirectory(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
    }

    public enum OSType {
        MAC("/Users/" + System.getProperty("user.name") + "/.DOControll/"),
        LIN("/home/" + System.getProperty("user.name") + "/.DOControl/"),
        WIN("C:\\DO\\");

        private String propertyFolder;

        OSType(String propertyFolder) {
            this.propertyFolder = propertyFolder;
        }

        public String getPropertyFolder() {
            return propertyFolder;
        }
    }
}
