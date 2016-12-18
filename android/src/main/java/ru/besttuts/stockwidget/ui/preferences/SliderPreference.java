package ru.besttuts.stockwidget.ui.preferences;

/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.besttuts.stockwidget.R;

/**
 * @author Jay Weisskopf
 */
public class SliderPreference extends DialogPreference {

    protected final static int SEEKBAR_RESOLUTION = 100;

    protected String mValue;
    protected int mSeekBarValue;
    protected CharSequence[] mSummaries;
    private boolean mValueSet;

    /**
     * @param context
     * @param attrs
     */
    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {
        setDialogLayoutResource(R.layout.slider_preference_dialog);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference);
        try {
            setSummary(a.getTextArray(R.styleable.SliderPreference_android_summary));
        } catch (Exception e) {
            // Do nothing
        }
        a.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
//        setValue(restoreValue ? getPersistedFloat(mValue) : (Float) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        int progress = Math.round(Integer.parseInt(mValue, 16) / 2.55f);

        switch (progress) {
            case 0:
                return getContext().getString(R.string.pref_slider_fully_transparent);
            case 100:
                return getContext().getString(R.string.pref_slider_fully_opaque);
            default:
                return progress + getContext().getString(R.string.pref_slider_visible);
        }
    }

    public void setSummary(CharSequence[] summaries) {
        mSummaries = summaries;
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        mSummaries = null;
    }

    @Override
    public void setSummary(int summaryResId) {
        try {
            setSummary(getContext().getResources().getString(summaryResId));
        } catch (Exception e) {
            super.setSummary(summaryResId);
        }
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        // Always persist/notify the first time.
        final boolean changed = !TextUtils.equals(mValue, value);
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistString(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    @Override
    protected View onCreateDialogView() {
        mSeekBarValue = Math.round(Integer.parseInt(mValue, 16) / 2.55f);
        View view = super.onCreateDialogView();
        final TextView textView = (TextView) view.findViewById(R.id.slider_preference_textView);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            textView.setTextColor(Color.WHITE);
        }
        textView.setText(getSummary());
        SeekBar seekbar = (SeekBar) view.findViewById(R.id.slider_preference_seekbar);
        seekbar.setMax(SEEKBAR_RESOLUTION);
        seekbar.setProgress(mSeekBarValue);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                switch (progress) {
                    case 0:
                        textView.setText(getContext().getString(R.string.pref_slider_fully_transparent));
                        break;
                    case 100:
                        textView.setText(getContext().getString(R.string.pref_slider_fully_opaque));
                        break;
                    default:
                        textView.setText(progress + getContext().getString(R.string.pref_slider_visible));
                        break;
                }
                SliderPreference.this.mSeekBarValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    switch (progress) {
                        case 0:
                            textView.setText(getContext().getString(R.string.pref_slider_fully_transparent));
                            break;
                        case 100:
                            textView.setText(getContext().getString(R.string.pref_slider_fully_opaque));
                            break;
                        default:
                            textView.setText(progress + getContext().getString(R.string.pref_slider_visible));
                            break;
                    }
                    SliderPreference.this.mSeekBarValue = progress;
                }
            }
        });
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        final String newValue = Integer.toHexString(Math.round(mSeekBarValue * 2.55f)) ;
        if (positiveResult && callChangeListener(newValue)) {
            setValue(newValue);
        }
        super.onDialogClosed(positiveResult);
    }

    // TODO: Save and restore preference state.
}