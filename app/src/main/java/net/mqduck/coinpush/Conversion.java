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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mqduck on 7/4/17.
 */

class Conversion
{
    private final static String DATUM_VALUE = "PRICE";
    private final static String DATUM_CHANGE = "CHANGEPCT24HOUR";
    
    private final static IntentFilter
            broadcastFilter = new IntentFilter("net.mqduck.coinpush.broadcast_database_reference_loaded");
    
    final Currency currencyFrom, currencyTo;
    final private Context context;
    private BroadcastReceiver broadCastReceiver;
    private DatabaseReference dbReference;
    private Double value = 0.0, change = 0.0;
    
    Conversion(final Currency currencyFrom, final Currency currencyTo, final Context context)
    {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.context = context;
        setdbReference();
        dbReference = ActivityMain
                .databaseReferenceConversionData
                .child(currencyFrom.code.toString())
                .child(currencyTo.code.toString());
    }
    
    Conversion(final Currency.Code codeFrom, final Currency.Code codeTo, final Context context)
    {
        currencyFrom = Currency.currencies.get(codeFrom);
        currencyTo = Currency.currencies.get(codeTo);
        this.context = context;
        setdbReference();
    }
    
    Conversion(final String codeStrFrom, final String codeStrTo, final Context context)
    {
        currencyFrom = Currency.currencies.get(Currency.Code.valueOf(codeStrFrom));
        currencyTo = Currency.currencies.get(Currency.Code.valueOf(codeStrTo));
        this.context = context;
        setdbReference();
    }
    
    private void setdbReference()
    {
        broadCastReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent)
            {
                dbReference = ActivityMain
                        .databaseReferenceConversionData
                        .child(currencyFrom.code.toString())
                        .child(currencyTo.code.toString());
                dbReference.addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        value = (Double)dataSnapshot.child("PRICE").getValue();
                        change = (Double)dataSnapshot.child("CHANGEPCT24HOUR").getValue();
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
        };
        registerReceiver();
    }
    
    void registerReceiver()
    {
        context.registerReceiver(broadCastReceiver, broadcastFilter);
    }
    
    void unregisterReceiver()
    {
        context.unregisterReceiver(broadCastReceiver);
    }
    
    public double getValue() { return value; }
    public double getChange() { return change; }
    
    String getKeyString()
    {
        return currencyFrom.code.toString() + ":" + currencyTo.code.toString();
    }
}
