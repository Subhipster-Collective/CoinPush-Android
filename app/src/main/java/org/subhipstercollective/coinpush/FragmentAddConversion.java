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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;

public class FragmentAddConversion extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_conversion, null);
    
        final AdapterCurrency adapterFrom = new AdapterCurrency(getActivity(), Currency.currencyListFrom);
        final AdapterCurrency adapterTo = new AdapterCurrency(getActivity(), Currency.currencyListTo);
        final Spinner spinnerFrom = (Spinner)view.findViewById(R.id.spinner_currency_from);
        final Spinner spinnerTo = (Spinner)view.findViewById(R.id.spinner_currency_to);
        
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);
        spinnerFrom.setSelection(ActivityMain.preferences
                                         .getInt(getString(R.string.key_preference_add_conversion_default_from), 0));
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapterTo);
        spinnerTo.setSelection(ActivityMain.preferences
                                       .getInt(getString(R.string.key_preference_add_conversion_default_to), 0));
        
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        Conversion conversion = new Conversion((Currency)spinnerFrom.getSelectedItem(),
                                                               (Currency)spinnerTo.getSelectedItem());
                        conversion.addListener();
                        ActivityMain.conversions.add(conversion);
                        
                        ActivityMain.preferencesEditor
                                .putString(getString(R.string.key_preference_conversions),
                                           ActivityMain.conversions.getConversionsString());
                        ActivityMain.preferencesEditor
                                .putInt(getString(R.string.key_preference_add_conversion_default_from),
                                        spinnerFrom.getSelectedItemPosition());
                        ActivityMain.preferencesEditor
                                .putInt(getString(R.string.key_preference_add_conversion_default_to),
                                        spinnerTo.getSelectedItemPosition());
                        ActivityMain.preferencesEditor.commit();
                        
                        DatabaseReference dbConversionPref = ActivityMain.databaseReferenceUser.child("conversionPrefs")
                                .child(conversion.getKeyString());
                        dbConversionPref.child("pushIncreased").setValue(false);
                        dbConversionPref.child("pushDecreased").setValue(false);
                        dbConversionPref.child("thresholdIncreased").setValue(ActivityMain.DEFAULT_THRESHOLD);
                        dbConversionPref.child("thresholdDecreased").setValue(ActivityMain.DEFAULT_THRESHOLD);
                    }
                })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override public void onClick(DialogInterface dialog, int which) {}
               });
        final AlertDialog dialog = builder.create();
        
        final Runnable setAddButtonEnabled = new Runnable() {
            @Override public void run()
            {
                boolean tf = spinnerFrom.getSelectedItem() == spinnerTo.getSelectedItem()
                             || ActivityMain.conversions.contains((Currency)spinnerFrom.getSelectedItem(),
                                                                  (Currency)spinnerTo.getSelectedItem());
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!tf);
            }
        };
        
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                setAddButtonEnabled.run();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                setAddButtonEnabled.run();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        return dialog;
    }
}
