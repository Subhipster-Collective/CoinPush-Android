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

package org.subhipstercollective.coinpush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class ActivityPreferencesConversion extends AppCompatActivity
{
    private final static float DEFAULT_THRESHOLD = 30.0f;
    private final static String FORMAT_STR_INCREASE_BASE = "When %s has increased by";
    private final static String FORMAT_STR_DECREASE_BASE = "When %s has decreased by";
    private final static String formatStrIncrease, formatStrDecrease;
    
    private TextView textConversion;
    private TextView textConversionValue;
    private TextView textConversionChange;
    private TextView textNotifyIncreased;
    private TextView textNotifyDecreased;
    private EditText editTextIncreased;
    private EditText editTextDecreased;
    private Button buttonRemove;
    private Button buttonSave;
    private CheckBox checkBoxIncreased;
    private CheckBox checkBoxDecreased;
    private AdView adView;
    
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
    
        textConversion = (TextView)findViewById(R.id.text_preferences_conversion);
        textConversionValue = (TextView)findViewById(R.id.text_preferences_conversion_value);
        textConversionChange = (TextView)findViewById(R.id.text_preferences_conversion_change);
        textNotifyIncreased = (TextView)findViewById(R.id.text_notify_increase);
        textNotifyDecreased = (TextView)findViewById(R.id.text_notify_decrease);
        editTextIncreased = (EditText)findViewById(R.id.edit_text_increased);
        editTextDecreased = (EditText)findViewById(R.id.edit_text_decreased);
        buttonRemove = (Button)findViewById(R.id.button_conversion_remove);
        buttonSave = (Button)findViewById(R.id.button_conversion_save);
        checkBoxIncreased = (CheckBox)findViewById(R.id.check_box_increased);
        checkBoxDecreased = (CheckBox)findViewById(R.id.check_box_decreased);
        adView = (AdView)findViewById(R.id.ad_view_preferences_conversion);
    
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        final Conversion conversion = ActivityMain
                .conversions
                .get(getIntent().getIntExtra(getString(R.string.key_intent_conversions_index), -1));
    
        final DatabaseReference conversionPrefs = ActivityMain
                .databaseReferenceUser
                .child("conversionPrefs")
                .child(conversion.getKeyString());
        
        boolean pushIncreasedEnabled, pushDecreasedEnabled;
        pushIncreasedEnabled = ActivityMain.preferences.getBoolean(conversion, R.string.key_preference_push_enabled_increased);
        pushDecreasedEnabled = ActivityMain.preferences.getBoolean(conversion, R.string.key_preference_push_enabled_decreased);
        editTextIncreased.setEnabled(pushIncreasedEnabled);
        checkBoxIncreased.setChecked(pushIncreasedEnabled);
        editTextDecreased.setEnabled(pushDecreasedEnabled);
        checkBoxDecreased.setChecked(pushDecreasedEnabled);
    
        editTextIncreased.setText(ActivityMain.preferences.getFloatStr(conversion,
                                                  R.string.key_preference_push_threshold_increase));
        editTextDecreased.setText(ActivityMain.preferences.getFloatStr(conversion,
                                                  R.string.key_preference_push_threshold_decrease));
        
        textConversion.setText(String.format(textConversion.getTag().toString(),
                conversion.currencyFrom.code,
                conversion.currencyTo.code));
        textConversionValue.setText( String.format(textConversionValue.getTag().toString(),
                conversion.currencyFrom.symbol,
                conversion.currencyTo.symbol,
                conversion.getValue()) );
        
        /*textConversionChange.setText( String.format(textConversionChange.getTag().toString(),
                conversion.getChange() > 0 ? "Up" : "Down",
                conversion.getChange()) );*/
        textConversionChange.setTextColor(conversion.getChangeColor());
        textConversionChange.setText( String.format(textConversionChange.getTag().toString(), conversion.getChange()) );
        
        textNotifyIncreased.setText(String.format(formatStrIncrease, conversion.currencyFrom.code));
        textNotifyDecreased.setText(String.format(formatStrDecrease, conversion.currencyFrom.code));
    
        if(ActivityMain.preferences.getBoolean(getString(R.string.key_preference_ads), false))
            loadAd();
    
        editTextIncreased.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean hasFocus)
            {
                verifyPushSetting(editTextIncreased, checkBoxIncreased);
            }
        });
        editTextDecreased.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean hasFocus)
            {
                verifyPushSetting(editTextDecreased, checkBoxDecreased);
            }
        });
    
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
                verifyPushSetting(editTextIncreased, checkBoxIncreased);
                verifyPushSetting(editTextDecreased, checkBoxDecreased);
    
                conversionPrefs.child("thresholdIncreased").setValue(Double.valueOf(editTextIncreased.getText().toString()));
                conversionPrefs.child("thresholdDecreased").setValue(Double.valueOf(editTextDecreased.getText().toString()));
                conversionPrefs.child("pushIncreased").setValue(checkBoxIncreased.isChecked());
                conversionPrefs.child("pushDecreased").setValue(checkBoxDecreased.isChecked());
    
                ActivityMain.preferencesEditor.putFloat(conversion, R.string.key_preference_push_threshold_increase,
                             editTextIncreased.getText().toString());
                ActivityMain.preferencesEditor.putFloat(conversion, R.string.key_preference_push_threshold_decrease,
                             editTextDecreased.getText().toString());
                ActivityMain.preferencesEditor.putBoolean(conversion, R.string.key_preference_push_enabled_increased, checkBoxIncreased.isChecked());
                ActivityMain.preferencesEditor.putBoolean(conversion, R.string.key_preference_push_enabled_decreased, checkBoxDecreased.isChecked());
                ActivityMain.preferencesEditor.commit();
                
                finish();
            }
        });
        
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                ActivityMain.preferencesEditor.remove(conversion, R.string.key_preference_push_threshold_increase);
                ActivityMain.preferencesEditor.remove(conversion, R.string.key_preference_push_threshold_decrease);
                ActivityMain.preferencesEditor.remove(conversion, R.string.key_preference_push_enabled_increased);
                ActivityMain.preferencesEditor.remove(conversion, R.string.key_preference_push_enabled_decreased);
                ActivityMain.conversions.remove(conversion);
                ActivityMain.conversionAdapter.notifyDataSetChanged();
                ActivityMain.preferencesEditor.putString(getString(R.string.key_preference_conversions),
                                                         ActivityMain.conversions.getConverionsString());
                ActivityMain.preferencesEditor.commit();
                
                conversionPrefs.removeValue();
                
                finish();
            }
        });
    }
    
    private void loadAd()
    {
        if(adView.getVisibility() == View.GONE)
        {
            adView.loadAd(ActivityMain.adRequestPrefsConversion);
            adView.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideAd()
    {
        adView.setVisibility(View.GONE);
    }
    
    @Override
    public void onWindowFocusChanged (boolean hasFocus)
    {
        int[] buttonRemovePos = new int[2];
        int[] adViewPos = new int[2];
        buttonRemove.getLocationInWindow(buttonRemovePos);
        adView.getLocationInWindow(adViewPos);
        
        if(buttonRemovePos[1] + buttonRemove.getHeight() > adViewPos[1])
            hideAd();
    }
    
    void verifyPushSetting(final EditText editText, final CheckBox checkBox)
    {
        if(editText.getText().toString().isEmpty() || Float.valueOf(editText.getText().toString()) <= 0)
        {
            editText.setText(String.format(Locale.getDefault(), Float.toString(DEFAULT_THRESHOLD)));
            editText.setEnabled(false);
            checkBox.setChecked(false);
        }
    }
}
