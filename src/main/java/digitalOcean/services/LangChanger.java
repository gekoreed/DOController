package digitalOcean.services;

import digitalOcean.controller.Control;

import java.util.Properties;

/**
 * Created by eshevchenko on 13.03.15 at 17:20.
 */
public class LangChanger {

    private static LangChanger langChanger;

    public static LangChanger getInstance() {
        if (langChanger == null)
            langChanger = new LangChanger();
        return langChanger;
    }

    enum lang {
        UA,
        RU,
        EN
    }


    public void changeLang(String lang, Control control) {
        Properties property = new Properties();
//        FileInputStream inStream;
//        try {
//
//            inStream = new FileInputStream("config/"+lang+".properties");
//
//            InputStreamReader inR = new InputStreamReader(inStream, "UTF-8");
//            property.load(inR);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        control.dropletName.setText(property.getProperty("droplet.name"));
//        control.refreshButton.setText(property.getProperty("ref.button"));
//        control.copyButton.setText(property.getProperty("copy.button"));
//        control.copyButton2.setText(property.getProperty("copy.button"));
//        control.dropletStatus.setText(property.getProperty("droplet.status"));
    }


}
