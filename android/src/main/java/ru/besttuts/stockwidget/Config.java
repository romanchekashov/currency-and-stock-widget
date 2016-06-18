package ru.besttuts.stockwidget;

import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 15.01.2015.
 */
public class Config {

    private static final String TAG = makeLogTag(Config.class);

    // General configuration
    public static final boolean IS_DEV_MODE = true;

    // Is this an internal dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = false;

    public static String getProperty(String className, String key){
        try {
            return (String) Class.forName(className).getField(key).get("");
        } catch (ClassNotFoundException e) {
            LOGE(TAG, e.getMessage());
        } catch (NoSuchFieldException e) {
            LOGE(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            LOGE(TAG, e.getMessage());
        }

        return "";
    }

}
