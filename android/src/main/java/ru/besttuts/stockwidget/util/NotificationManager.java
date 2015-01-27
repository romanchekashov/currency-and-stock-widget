package ru.besttuts.stockwidget.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 27.01.2015.
 */
public class NotificationManager {

    public interface ColorChangedListener {
        void changeColor();
    }

    private static List<ColorChangedListener> listeners = new ArrayList<>();

    public static void addListener(ColorChangedListener listener) {
        if (null == listeners) listeners = new ArrayList<>();

        listeners.add(listener);
    }

    public static void removeListener(ColorChangedListener listener) {
        if (null == listeners || listeners.isEmpty()) {
            return;
        }

        listeners.remove(listener);
    }

    public static void notifyColorChangedListeners() {
        if (null == listeners || listeners.isEmpty()) {
            return;
        }

        for (ColorChangedListener listener: listeners) {
            listener.changeColor();
        }
    }
}
