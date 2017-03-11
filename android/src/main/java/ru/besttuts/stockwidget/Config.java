package ru.besttuts.stockwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 15.01.2015.
 */
public class Config {

    private static final String TAG = makeLogTag(Config.class);

    // General configuration
    public static final boolean IS_DEV_MODE = false;

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

    public static final int DEVICE_API_VERSION = android.os.Build.VERSION.SDK_INT;
    public static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";
    public static final String PREF_PREFIX_KEY = "appwidget_";
    public static int CURRENT_APP_WIDGET_ID;
    private static Config configInstance;
    private Context mContext;

    private String deviceLanguage;

    private Config(Context context){
        mContext = context;
        setLanguage(context);
    }

    public static void init(Context context) {
        if (null == configInstance) {
            configInstance = new Config(context);
        }
    }

    public static Config getInstance() {
        if(null == configInstance){
            throw new RuntimeException("before use call Config.init(context) from Application.onCreate()");
        }
        return configInstance;
    }

    @SuppressLint("NewApi")
    private void setLanguage(Context context){
        Configuration configuration = context.getResources().getConfiguration();

        if (DEVICE_API_VERSION >= Build.VERSION_CODES.N){
            deviceLanguage = configuration.getLocales().get(0).getLanguage();
        } else {
            deviceLanguage = configuration.locale.getLanguage();
        }
    }

    public String getLanguage(){
        return deviceLanguage;
    }

    private static final String WIDGET_ITEM_POSITION = "widgetItemPosition";
    public void setWidgetItemPosition(int widgetItemPosition) {
        SharedPreferences.Editor prefs = mContext.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(createPrefKey(WIDGET_ITEM_POSITION), widgetItemPosition);
        prefs.commit();
    }

    public int getWidgetItemPosition() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(createPrefKey(WIDGET_ITEM_POSITION), 1);
    }

    private static String createPrefKey(String keyName){
        StringBuilder sb = new StringBuilder();
        sb.append(PREF_PREFIX_KEY);
        sb.append(CURRENT_APP_WIDGET_ID);
        sb.append("_");
        sb.append(keyName);
        return sb.toString();
    }
}
