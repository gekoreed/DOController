package digitalOcean.dao;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Propertie {

    private static Preferences preferences = Preferences.userRoot().node("docontroller");

    /**
     * Get API-key from properties file.
     * If file doesn't exist method will create it with empty
     * API-key property
     *
     * @return API-key
     */
    public static String getLogin() {
        String login = preferences.get("login", null);
        if (login == null) {
            clearProperties();
            login = "new";
        }
        return login;
    }

    public static void clearProperties() {
        preferences.put("api", "new");
        preferences.put("authType", "rsa");
        preferences.put("login", "new");
        preferences.put("autoUpdate", "yes");
        preferences.put("interval", "10");
    }


    /**
     * Used when the app start at first time
     * or when user wants to change existing API-key
     *
     * @param login new API-key
     */
    public static void updateLoginInPropertieFile(String login) {
        preferences.put("login", login);
    }

    public static void updateKey(String key) {
        preferences.put("api", key);
    }

    public static void addNewPropertie(String key, String value) {
        preferences.put(key, value);
    }


    public static String getPropertie(String key) {
        return preferences.get(key, "10");
    }


    public static String getApiKeyFromConfig() throws IOException {
        return getPropertie("api");
    }

    public static void autoUpdate(boolean autoUpdate) {
        if (autoUpdate)
            addNewPropertie("autoUpdate", "yes");
        else
            addNewPropertie("autoUpdate", "no");
    }
}
