package ru.besttuts.stockwidget.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.EconomicWidgetConfigureActivity;

/**
 * see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 * Created by roman on 26.02.2015.
 */
public class AppRater {
    private final static String APP_PNAME = "ru.besttuts.stockwidget";

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;

    public static void app_launched(Context mContext, FragmentManager fragmentManager) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0);
//        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            showRateDialog(fragmentManager, mContext, editor);
        } else if (System.currentTimeMillis() >= date_firstLaunch +
                (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
            showRateDialog(fragmentManager, mContext, editor);
        }

        editor.commit();
    }

    public static void countLaunches(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);
        editor.commit();
    }

    public static void showRateDialog(FragmentManager fragmentManager, final Context mContext, final SharedPreferences.Editor editor) {

        DialogFragment dialogFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View view = inflater.inflate(R.layout.dialog_app_rater, null);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    view.setBackgroundColor(Color.WHITE);
                }
                view.findViewById(R.id.btnRateNow).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dismiss();
                    }
                });
                view.findViewById(R.id.btnRateLater).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                view.findViewById(R.id.btnRateNo).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dismiss();
                    }
                });
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view);

                return builder.create();
            }
        };
        dialogFragment.show(fragmentManager, null);

    }
}