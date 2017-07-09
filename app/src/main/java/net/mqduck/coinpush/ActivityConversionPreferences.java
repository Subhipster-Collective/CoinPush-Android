/*
 * Copyright 2017 Jeffrey Thomas Piercy
 *
 * This file is part of CoinPush.
 *
 * CoinPush is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CoinPush is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CoinPush.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mqduck.coinpush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class ActivityConversionPreferences extends AppCompatActivity
{
    private final static float DEFAULT_THRESHOLD = 10.0f;
    
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
        
        final Conversion conversion = ActivityMain
                .conversions
                .get(getIntent().getIntExtra(getString(R.string.key_intent_conversions_index), -1));
    
        final String preferencesKeyIncrease = getString(R.string.key_preference_push_increase)
                                              + conversion.currencyFrom.code.toString()
                                              + ":"
                                              + conversion.currencyTo.code.toString();
        final String preferencesKeyDecrease = getString(R.string.key_preference_push_decrease)
                                              + conversion.currencyFrom.code.toString()
                                              + ":"
                                              + conversion.currencyTo.code.toString();
    
        editTextIncreased.setText(String.format(Locale.getDefault(), "%f", ActivityMain.preferences
                .getFloat(preferencesKeyIncrease, DEFAULT_THRESHOLD)));
        editTextDecreased.setText(String.format(Locale.getDefault(), "%f", ActivityMain.preferences
                .getFloat(preferencesKeyDecrease, DEFAULT_THRESHOLD)));
        
        textConversion.setText(String.format(textConversion.getTag().toString(),
                                             conversion.currencyFrom.code,
                                             conversion.currencyTo.code));
        textConversionValue.setText(String.format(textConversionValue.getTag().toString(),
                                             conversion.currencyFrom.symbol,
                                             conversion.currencyTo.symbol,
                                             conversion.getValue()));
        textNotifyIncrease.setText(String.format(textNotifyIncrease.getTag().toString(),
                                                 conversion.currencyFrom.code));
        textNotifyDecrease.setText(String.format(textNotifyDecrease.getTag().toString(),
                                                 conversion.currencyFrom.code));
        
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                ActivityMain.preferencesEditor.putFloat( preferencesKeyIncrease,
                                                         Float.valueOf(editTextIncreased.getText().toString()) );
                ActivityMain.preferencesEditor.putFloat( preferencesKeyDecrease,
                                                         Float.valueOf(editTextDecreased.getText().toString()) );
                ActivityMain.preferencesEditor.commit();
            }
        });
        
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                
            }
        });
    }
}
