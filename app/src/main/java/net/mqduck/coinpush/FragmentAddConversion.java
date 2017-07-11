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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

/**
 * Created by mqduck on 7/8/17.
 */

public class FragmentAddConversion extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_conversion, null);
    
        final CurrencyAdapter adapterFrom = new CurrencyAdapter(getActivity(), Currency.currencyListFrom);
        final CurrencyAdapter adapterTo = new CurrencyAdapter(getActivity(), Currency.currencyListTo);
        final Spinner spinnerFrom = (Spinner)view.findViewById(R.id.spinner_currency_from);
        final Spinner spinnerTo = (Spinner)view.findViewById(R.id.spinner_currency_to);
        
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);
        spinnerTo.setAdapter(adapterTo);
        spinnerFrom.setSelection(ActivityMain.preferences
                                         .getInt(getString(R.string.key_preference_add_conversion_default_from), 0));
        spinnerTo.setSelection(ActivityMain.preferences
                                       .getInt(getString(R.string.key_preference_add_conversion_default_to), 0));
        
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityMain.conversions.add(new Conversion((Currency)spinnerFrom.getSelectedItem(),
                                                                    (Currency)spinnerTo.getSelectedItem()));
                        ActivityMain.conversionAdapter.notifyDataSetChanged();
                        ActivityMain.updateData();
                        ActivityMain.preferencesEditor
                                .putString(getString(R.string.key_preference_conversions),
                                           ActivityMain.conversions.getConverionsString());
                        ActivityMain.preferencesEditor
                                .putInt(getString(R.string.key_preference_add_conversion_default_from),
                                        spinnerFrom.getSelectedItemPosition());
                        ActivityMain.preferencesEditor
                                .putInt(getString(R.string.key_preference_add_conversion_default_to),
                                        spinnerTo.getSelectedItemPosition());
                        ActivityMain.preferencesEditor.commit();
                    }
                })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override public void onClick(DialogInterface dialog, int which)
                   {
                       
                   }
               });
        
        return builder.create();
    }
}
