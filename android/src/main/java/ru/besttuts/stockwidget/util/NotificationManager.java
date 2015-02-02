package ru.besttuts.stockwidget.util;

import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 27.01.2015.
 */
public class NotificationManager {

    public interface Listener {
    }

    public interface ColorChangedListener extends Listener {
        void changeColor();
    }

    public interface OptionsItemSelectListener extends Listener {
        void onOptionsItemSelectedInActivity(MenuItem item);
    }

    private static List<ColorChangedListener> colorChangedListeners = new ArrayList<>();
    private static List<OptionsItemSelectListener> optionsItemSelectListeners = new ArrayList<>();

    public static void addListener(Listener listener) {
        if (listener instanceof ColorChangedListener) {
            addColorChangedListener((ColorChangedListener) listener);
        }
        if (listener instanceof OptionsItemSelectListener) {
            addOptionsItemSelectListener((OptionsItemSelectListener) listener);
        }
    }

    public static void removeListener(Listener listener) {
        if (listener instanceof ColorChangedListener) {
            removeColorChangedListener((ColorChangedListener) listener);
        }
        if (listener instanceof OptionsItemSelectListener) {
            removeOptionsItemSelectListener((OptionsItemSelectListener) listener);
        }
    }

    public static void addColorChangedListener(ColorChangedListener listener) {
        if (null == colorChangedListeners) colorChangedListeners = new ArrayList<>();

        colorChangedListeners.add(listener);
    }

    public static void removeColorChangedListener(ColorChangedListener listener) {
        if (null == colorChangedListeners || colorChangedListeners.isEmpty()) {
            return;
        }

        colorChangedListeners.remove(listener);
    }

    public static void notifyColorChangedListeners() {
        if (null == colorChangedListeners || colorChangedListeners.isEmpty()) {
            return;
        }

        for (ColorChangedListener listener: colorChangedListeners) {
            listener.changeColor();
        }
    }

    // --------------------------------------------------------------------------

    public static void addOptionsItemSelectListener(OptionsItemSelectListener listener) {
        if (null == optionsItemSelectListeners) optionsItemSelectListeners = new ArrayList<>();

        optionsItemSelectListeners.add(listener);
    }

    public static void removeOptionsItemSelectListener(OptionsItemSelectListener listener) {
        if (null == optionsItemSelectListeners || optionsItemSelectListeners.isEmpty()) {
            return;
        }

        optionsItemSelectListeners.remove(listener);
    }

    public static void notifyOptionsItemSelected(MenuItem item) {
        if (null == optionsItemSelectListeners || optionsItemSelectListeners.isEmpty()) {
            return;
        }

        for (OptionsItemSelectListener listener: optionsItemSelectListeners) {
            listener.onOptionsItemSelectedInActivity(item);
        }
    }

}
