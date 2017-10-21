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

import android.graphics.Color;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

class Conversion
{
    final static double COLOR_SCALE = 255.0 / 30.0;
    
    final Currency currencyFrom, currencyTo;
    private Double value = 0.0, change = 0.0;
    private boolean dataUninitialized = true;
    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override public void onDataChange(DataSnapshot dataSnapshot)
        {
            value = Double.valueOf(dataSnapshot.child("PRICE").getValue().toString());
            change = Double.valueOf(dataSnapshot.child("CHANGEPCT24HOUR").getValue().toString());
            if(dataUninitialized)
            {
                ActivityMain.adapterConversion.notifyDataSetChanged();
                dataUninitialized = false;
            }
        }
        @Override public void onCancelled(DatabaseError databaseError) {}
    };
    
    Conversion(final Currency currencyFrom, final Currency currencyTo)
    {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }
    
    Conversion(final Currency.Code codeFrom, final Currency.Code codeTo)
    {
        currencyFrom = Currency.currencies.get(codeFrom);
        currencyTo = Currency.currencies.get(codeTo);
    }
    
    Conversion(final String codeStrFrom, final String codeStrTo)
    {
        currencyFrom = Currency.currencies.get(Currency.Code.valueOf(codeStrFrom));
        currencyTo = Currency.currencies.get(Currency.Code.valueOf(codeStrTo));
    }
    
    void addListener()
    {
        if(ActivityMain.databaseReferenceConversionData != null)
            ActivityMain.databaseReferenceConversionData
                    .child(currencyFrom.code.toString())
                    .child(currencyTo.code.toString())
                    .addValueEventListener(eventListener);
    }
    
    void removeListener()
    {
        if(ActivityMain.databaseReferenceConversionData != null)
            ActivityMain.databaseReferenceConversionData
                    .child(currencyFrom.code.toString())
                    .child(currencyTo.code.toString())
                    .removeEventListener(eventListener);
    }
    
    double getValue() { return value; }
    double getChange() { return change; }
    
    String getKeyString()
    {
        return currencyFrom.code.toString() + ":" + currencyTo.code.toString();
    }
    
    int getChangeColor()
    {
        if(change < 0)
        {
            int red = (int)Math.round(-change * COLOR_SCALE);
            return Color.rgb(red > 255 ? 255 : red, 0, 0);
        }
        else
        {
            int green = (int)Math.round(change * COLOR_SCALE);
            return Color.rgb(0, green > 255 ? 255 : green, 0);
        }
    }
}
