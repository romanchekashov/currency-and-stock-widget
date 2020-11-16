package ru.besttuts.stockwidget.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;

import androidx.appcompat.app.ActionBar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.QuoteType;
import ru.besttuts.stockwidget.ui.fragments.ConfigPreferenceFragment;

import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 27.01.2015.
 */
public class Utils {

    private static final String TAG = makeLogTag(Utils.class);

    public static String getModelNameFromResourcesBySymbol(Context context, Model model) {

        if (null == model) return "-";
        if (null == model.getSymbol()) return model.getName();

        if (QuoteType.COMMODITY == model.getQuoteType()) {
            String symbol = model.getSymbol();
            String field = symbol.toLowerCase().substring(0, symbol.length() - 7);
            try {
                return context.getString(R.string.class.getDeclaredField(field).getInt(null));
            } catch (IllegalAccessException e) {
                LOGE(TAG, e.getMessage());
            } catch (NoSuchFieldException e) {
                LOGE(TAG, e.getMessage());
            }
        }

        return model.getName();
    }

    public static String getModelNameFromResourcesBySymbol(Context context, String symbol) {
        return getModelNameFromResourcesBySymbol(context, QuoteType.COMMODITY, symbol);
    }

    public static String getModelNameFromResourcesBySymbol(Context context, int quoteType,
                                                           String symbol) {
        if (null == context || null == symbol || symbol.isEmpty()) return "-";

        if (QuoteType.CURRENCY == quoteType) {
            return symbol.substring(0, 3) + "/" + symbol.substring(3);
        }

        if (QuoteType.COMMODITY != quoteType) {
            return symbol;
        }

        String field = symbol.toLowerCase().substring(0, symbol.length() - 7);
        try {
            return context.getString(R.string.class.getDeclaredField(field).getInt(null));
        } catch (IllegalAccessException e) {
            LOGE(TAG, e.getMessage());
        } catch (NoSuchFieldException e) {
            LOGE(TAG, e.getMessage());
        }

        return symbol;
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String style = sharedPref.getString("theme", "light");
        if ("light".equals(style)) {
            activity.setTheme(R.style.AppTheme_Light);
        } else {
            activity.setTheme(R.style.AppTheme_Dark);
        }
    }

    public static void onActivityCreateSetActionBarColor(ActionBar actionBar) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(actionBar.getThemedContext());
        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
    }

    public static String getNYSEInfo(Context context) {
        DateTimeZone nyTZ = DateTimeZone.forID("America/New_York");
        DateTime now = new DateTime(nyTZ);
        int[] daysHoursMinutes;
        switch (now.getDayOfWeek()) {
            case DateTimeConstants.SATURDAY:
                DateTime temp = now.plusDays(2);
                DateTime nextTradeStart = new DateTime(temp.getYear(), temp.getMonthOfYear(),
                        temp.getDayOfMonth(), 9, 30, nyTZ);
                temp = nextTradeStart.minus(now.getMillis());
                daysHoursMinutes = getDaysHoursMinutes(temp.getMillis());
                return String.format(context.getString(R.string.nyse_is_closed), daysHoursMinutes[0],
                        daysHoursMinutes[1], daysHoursMinutes[2]);
            case DateTimeConstants.SUNDAY:
                temp = now.plusDays(1);
                nextTradeStart = new DateTime(temp.getYear(), temp.getMonthOfYear(),
                        temp.getDayOfMonth(), 9, 30, nyTZ);
                temp = nextTradeStart.minus(now.getMillis());
                daysHoursMinutes = getDaysHoursMinutes(temp.getMillis());
                return String.format(context.getString(R.string.nyse_is_closed), daysHoursMinutes[0],
                        daysHoursMinutes[1], daysHoursMinutes[2]);
            default:
                nextTradeStart = new DateTime(now.getYear(), now.getMonthOfYear(),
                        now.getDayOfMonth(), 9, 30, nyTZ);
                DateTime nextTradeEnd = new DateTime(now.getYear(), now.getMonthOfYear(),
                        now.getDayOfMonth(), 16, 0, nyTZ);
                if (now.isBefore(nextTradeStart.getMillis())) {
                    temp = nextTradeStart.minus(now.getMillis());
                    daysHoursMinutes = getDaysHoursMinutes(temp.getMillis());
                    return String.format(context.getString(R.string.nyse_is_closed), daysHoursMinutes[0],
                            daysHoursMinutes[1], daysHoursMinutes[2]);
                }
                if (now.isAfter(nextTradeEnd.getMillis())) {
                    temp = nextTradeStart.plusDays(1).minus(now.getMillis());
                    daysHoursMinutes = getDaysHoursMinutes(temp.getMillis());
                    return String.format(context.getString(R.string.nyse_is_closed), daysHoursMinutes[0],
                            daysHoursMinutes[1], daysHoursMinutes[2]);
                }
                temp = nextTradeEnd.minus(now.getMillis());
                daysHoursMinutes = getDaysHoursMinutes(temp.getMillis());
                return String.format(context.getString(R.string.nyse_is_open), daysHoursMinutes[0],
                        daysHoursMinutes[1], daysHoursMinutes[2]);
        }
    }

    public static String encodeYahooApiQuery(String query) {
        String encoded = query.replace(" ", "%20");
        encoded = encoded.replace("\"", "%22");
        encoded = encoded.replace("=", "%3D");
        encoded = encoded.replace(";", "%3B%0A");
        return encoded.replace(",", "%2C");
    }

    private static int[] getDaysHoursMinutes(long time) {
        int[] dhm = new int[3];
        time /= 1000;
        time /= 60; // time in min
        dhm[2] = (int) time % 60; // get min
        time /= 60; // time in hours
        dhm[1] = (int) time % 24; // get hours
        time /= 24;
        dhm[0] = (int) time;

        return dhm;
    }

    public static <T> String join(String delimiter, Collection<T> elements) {
        if (elements == null || elements.size() == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (T t : elements) sb.append(t).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    public static Set<Integer> split(String delimiter, String elements) {
        if (elements == null || elements.length() == 0) return null;
        String[] arr = elements.split(delimiter);
        Set<Integer> collection = new HashSet<>(arr.length);
        for (String el : arr) collection.add(Integer.parseInt(el));
        return collection;
    }
}
