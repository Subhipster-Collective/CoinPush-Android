/*
 * Copyright 2017 Jeffrey Thomas Piercy
 *
 * This file is part of CoinPush-Android.
 *
 * CoinPush-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CoinPush-Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CoinPush-Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mqduck.coinpush;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class ActivityPreferencesConversion extends AppCompatActivity
{
    private final static float DEFAULT_THRESHOLD = 10.0f;
    private final static String FORMAT_STR_INCREASE_BASE = "When %s has increased by";
    private final static String FORMAT_STR_DECREASE_BASE = "When %s has decreased by";
    private final static String formatStrIncrease, formatStrDecrease;
    
    static
    {
        if(android.os.Build.VERSION.SDK_INT > 22)
        {
            formatStrIncrease = "\uD83D\uDCC8 " + FORMAT_STR_INCREASE_BASE;
            formatStrDecrease = "\uD83D\uDCC9 " + FORMAT_STR_DECREASE_BASE;
        }
        else
        {
            formatStrIncrease = FORMAT_STR_INCREASE_BASE;
            formatStrDecrease = FORMAT_STR_DECREASE_BASE;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_preferences);
    
        final TextView textConversion = (TextView)findViewById(R.id.text_preferences_conversion);
        final TextView textConversionValue = (TextView)findViewById(R.id.text_preferences_conversion_value);
        final TextView textNotifyIncrease = (TextView)findViewById(R.id.text_notify_increase);
        final TextView textNotifyDecrease = (TextView)findViewById(R.id.text_notify_decrease);
        final EditText editTextIncreased = (EditText)findViewById(R.id.edit_text_increased);
        final EditText editTextDecreased = (EditText)findViewById(R.id.edit_text_decreased);
        final Button buttonRemove = (Button)findViewById(R.id.button_conversion_remove);
        final Button buttonSave = (Button)findViewById(R.id.button_conversion_save);
        final CheckBox checkBoxIncreased = (CheckBox)findViewById(R.id.check_box_increased);
        final CheckBox checkBoxDecreased = (CheckBox)findViewById(R.id.check_box_decreased);
    
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        final Conversion conversion = ActivityMain
                .conversions
                .get(getIntent().getIntExtra(getString(R.string.key_intent_conversions_index), -1));
    
        final DatabaseReference dbReference = ActivityMain
                .databaseReference
                .child("conversions")
                .child(conversion.getKeyString());
        
        boolean pushIncreasedEnabled, pushDecreasedEnabled;
        pushIncreasedEnabled = getPrefBool(conversion, R.string.key_preference_push_enabled_increase);
        pushDecreasedEnabled = getPrefBool(conversion, R.string.key_preference_push_enabled_decrease);
        editTextIncreased.setEnabled(pushIncreasedEnabled);
        checkBoxIncreased.setChecked(pushIncreasedEnabled);
        editTextDecreased.setEnabled(pushDecreasedEnabled);
        checkBoxDecreased.setChecked(pushDecreasedEnabled);
    
        editTextIncreased.setText(getPrefFloatStr(conversion,
                                                  R.string.key_preference_push_threshold_increase,
                                                  DEFAULT_THRESHOLD));
        editTextDecreased.setText(getPrefFloatStr(conversion,
                                                  R.string.key_preference_push_threshold_decrease,
                                                  DEFAULT_THRESHOLD));
        
        textConversion.setText(String.format(textConversion.getTag().toString(),
                                             conversion.currencyFrom.code,
                                             conversion.currencyTo.code));
        textConversionValue.setText( String.format(textConversionValue.getTag().toString(),
                                             conversion.currencyFrom.symbol,
                                             conversion.currencyTo.symbol,
                                             conversion.getValue()) );
        textNotifyIncrease.setText(String.format(formatStrIncrease, conversion.currencyFrom.code));
        textNotifyDecrease.setText(String.format(formatStrDecrease, conversion.currencyFrom.code));
    
        checkBoxIncreased.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                editTextIncreased.setEnabled(checkBoxIncreased.isChecked());
            }
        });
    
        checkBoxDecreased.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                editTextDecreased.setEnabled(checkBoxDecreased.isChecked());
            }
        });
        
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                setPrefFloat(conversion, R.string.key_preference_push_threshold_increase,
                             editTextIncreased.getText().toString());
                setPrefFloat(conversion, R.string.key_preference_push_threshold_decrease,
                             editTextDecreased.getText().toString());
                setPrefBool(conversion, R.string.key_preference_push_enabled_increase, checkBoxIncreased.isChecked());
                setPrefBool(conversion, R.string.key_preference_push_enabled_decrease, checkBoxDecreased.isChecked());
                ActivityMain.preferencesEditor.commit();
    
                dbReference.child("threatholdIncreased").setValue(Double.valueOf(editTextIncreased.getText().toString()));
                dbReference.child("threatholdDecreased").setValue(Double.valueOf(editTextDecreased.getText().toString()));
                dbReference.child("pushIncreased").setValue(checkBoxIncreased.isChecked());
                dbReference.child("pushDecreased").setValue(checkBoxDecreased.isChecked());
                
                finish();
            }
        });
        
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                removePref(conversion, R.string.key_preference_push_threshold_increase);
                removePref(conversion, R.string.key_preference_push_threshold_decrease);
                removePref(conversion, R.string.key_preference_push_enabled_increase);
                removePref(conversion, R.string.key_preference_push_enabled_decrease);
                ActivityMain.conversions.remove(conversion);
                ActivityMain.conversionAdapter.notifyDataSetChanged();
                ActivityMain.preferencesEditor.putString(getString(R.string.key_preference_conversions),
                                                         ActivityMain.conversions.getConverionsString());
                ActivityMain.preferencesEditor.commit();
                
                dbReference.removeValue();
                
                finish();
            }
        });
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        if(ActivityMain.preferences.getBoolean(getString(R.string.key_preference_ads), false))
            ((FrameLayout)findViewById(R.id.ad_frame_preferences_conversion))
                    .addView(ActivityMain.adViewPrefsConversion);
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        ((FrameLayout)findViewById(R.id.ad_frame_preferences_conversion)).removeAllViews();
    }
    
    String getPrefKeyStr(final @StringRes int preferenceyKey, final Conversion conversion)
    {
        return getString(preferenceyKey) + conversion.getKeyString();
    }
    
    void setPrefBool(final Conversion conversion, final @StringRes int preferenceKey, final boolean value)
    {
        ActivityMain.preferencesEditor.putBoolean(getPrefKeyStr(preferenceKey, conversion), value);
    }
    
    boolean getPrefBool(final Conversion conversion, final @StringRes int preferenceKey, final boolean defValue)
    {
        return ActivityMain.preferences.getBoolean(getPrefKeyStr(preferenceKey, conversion), defValue);
    }
    
    boolean getPrefBool(final Conversion conversion, final @StringRes int preferenceKey)
    {
        return getPrefBool(conversion, preferenceKey, false);
    }
    
    void setPrefFloat(final Conversion conversion, final @StringRes int preferenceKey, final String valueStr)
    {
        ActivityMain.preferencesEditor.putFloat(getPrefKeyStr(preferenceKey, conversion), Float.valueOf(valueStr));
    }
    
    String getPrefFloatStr(final Conversion conversion, final @StringRes int preferenceKey, final float defValue)
    {
        return String.format(Locale.getDefault(), "%.2f",
                             ActivityMain.preferences.getFloat(getPrefKeyStr(preferenceKey, conversion), defValue));
        
    }
    
    void removePref(final Conversion conversion, final @StringRes int preferenceKey)
    {
        ActivityMain.preferencesEditor.remove(getPrefKeyStr(preferenceKey, conversion));
    }
}
