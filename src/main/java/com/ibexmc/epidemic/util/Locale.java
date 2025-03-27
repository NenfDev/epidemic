package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locale {

    /**
     * Gets the text for the code provided from the Locale class which pulls it from the lang.yml
     * file.  If the code is not found in the file, it should use the defaultText provided.
     * It also loops through all entries in the placeHolder map provided and replaces the <% %> codes
     * with the value.
     * @param code Locale code
     * @param defaultText Default text
     * @param placeHolder Map of String String with code, text
     * @return Language specific string
     */
    public static String localeText(String code, String defaultText, HashMap<String, String> placeHolder) {
        Epidemic instance = Epidemic.instance();
        String returnValue = defaultText;
        if (instance.gameData().locale().get() != null) {
            if (instance.gameData().locale().has(code)) {
                String newMessage = instance.gameData().locale().get(code);
                if (!newMessage.equals("")) {
                    returnValue = newMessage;
                }
            }
            if (placeHolder != null) {
                for (Map.Entry<String, String> params : placeHolder.entrySet()) {
                    returnValue = returnValue.replace(params.getKey(), params.getValue());
                }
            }
        }
        return StringFunctions.colorToString(returnValue);
    }

    /**
     * Loads the data from lang.yml into the locale map
     */
    public static void load() {
        Epidemic instance = Epidemic.instance();
        File file = new File(Epidemic.instance().getDataFolder() + "/lang.yml");
        if (file.exists()) {
            Map<String, String> locale = new HashMap<>();
            ConfigParser configParser = new ConfigParser(Epidemic.instance(), file);
            List<String> keys = configParser.getKeys("");
            for (String key : keys) {
                ConfigParser.ConfigReturn crLocale = configParser.getStringValue(
                        key,
                        "",
                        false
                );
                if (crLocale.isSuccess()) {
                    locale.put(key, crLocale.getString());
                }
            }
            instance.gameData().locale().set(locale);
        } else {
            // error
        }
    }

}
