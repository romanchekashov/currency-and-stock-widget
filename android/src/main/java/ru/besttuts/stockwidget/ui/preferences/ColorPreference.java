package ru.besttuts.stockwidget.ui.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.ConfigPreferenceFragment;
import ru.besttuts.stockwidget.ui.view.CircleView;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 25.01.2015.
 */
public class ColorPreference extends ListPreference {

    private static final String TAG = makeLogTag(ColorPreference.class);

    Context mContext;
    private LayoutInflater mInflater;
    CharSequence[] entries;
    CharSequence[] mEntryValues;
    List<RadioButton> rButtonList;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private int mClickedDialogEntryIndex;


    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        rButtonList = new ArrayList<RadioButton>();
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = prefs.edit();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        entries = getEntries();
        mEntryValues = getEntryValues();
        mClickedDialogEntryIndex = findIndexOfValue(prefs.getString(
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE));

        if (entries == null || mEntryValues == null || entries.length != mEntryValues.length ) {
            throw new IllegalStateException("ListPreference requires an entries array " +
                    "and an mEntryValues array which are both the same length");
        }

//        for (int i = 0; i < entries.length; i++) {
//            LOGD(TAG, "onPrepareDialogBuilder: " + String.format("pos = %d, color = %s, text = %s",
//                    i, mEntryValues[i], entries[i]));
//        }

        ListAdapter listAdapter = new ColorArrayAdapter(getContext(),
                android.R.layout.list_content, getEntryValues(), mClickedDialogEntryIndex, this);

        builder.setAdapter(listAdapter, this);

//        builder.setAdapter(new ColorPreferenceAdapter(), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

//        builder.setSingleChoiceItems(new ColorPreferenceAdapter(), mClickedDialogEntryIndex,
//                new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mClickedDialogEntryIndex = which;
//
//                        /*
//                         * Clicking on an item simulates the positive button
//                         * click, and dismisses the dialog.
//                         */
//                ColorPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
//                dialog.dismiss();
//            }
//        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }

    }

    public void SetResult(int position) {
        editor.putString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                String.valueOf(mEntryValues[position]));
        editor.commit();
        this.getDialog().dismiss();
    }

    private class ColorArrayAdapter extends ArrayAdapter<CharSequence> implements View.OnClickListener {

        int index;
        ColorPreference ts;

        private ColorArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects,
                                  int selected, ColorPreference ts) {
            super(context, textViewResourceId, objects);
            index = selected;
            this.ts = ts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //get themeId
            CharSequence themeId = this.getItem(position);

            //inflate layout
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            View row = inflater.inflate(R.layout.color_prefs_item, parent, false);
            row.setId(position);

            //set on click listener for row
            row.setOnClickListener(this);

            CircleView mCircleView = null;
            TextView text = null;
            RadioButton rButton = null;

            mCircleView = (CircleView) row.findViewById(R.id.circleView);
            mCircleView.setColor(Color.parseColor((String) mEntryValues[position]));

            text = (TextView)row.findViewById(R.id.tvColorPrefs);
            text.setText(entries[position]);

            rButton = (RadioButton)row.findViewById(R.id.rbColorPrefs);
//            rButton.setId(position);
            if (position == index) {
                rButton.setChecked(true);
            }
            rButton.setClickable(false);

            return row;
        }

        @Override
        public void onClick(View v) {
            ts.SetResult(v.getId());
        }
    }

}
