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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class ActivityMain extends AppCompatActivity
{
    private final static String APP_ID = "ca-app-pub-9926113995373020~9860570594";
    //private final static String AD_UNIT_ID_MAIN = "ca-app-pub-9926113995373020/3674436196";
    //private final static String AD_UNIT_ID_CONVERSION = "ca-app-pub-9926113995373020/8551960990";
    
    private ValueEventListener eventListener = new ValueEventListener() {
        @Override public void onDataChange(DataSnapshot dataSnapshot)
        {
            conversionAdapter.notifyDataSetChanged(); // Add code to skip this call on first run?
        }
        @Override public void onCancelled(DatabaseError databaseError) {}
    };
    
    static ConversionList conversions;// = new ConversionList();
    static ConversionAdapter conversionAdapter;
    static float emojiSize;
    static CoinPushPreferences preferences;
    static CoinPushPreferences.Editor preferencesEditor;
    static AdView adViewMain;;
    static AdRequest adRequestMain, adRequestPrefsConversion;
    static boolean mobileAdsUninitialized = true;
    static FirebaseAuth auth;
    static FirebaseUser user;
    static FirebaseDatabase database;
    static DatabaseReference databaseReferenceConversionData;
    static DatabaseReference databaseReferenceConversionDataTimestamp;
    static DatabaseReference databaseReferenceUser;
    
    private Toolbar toolbar;
    private ListView list;
    private FrameLayout adFrameMain;
    private Boolean preferencesSynced = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        emojiSize = (float)0.7 * getResources().getDrawable(R.mipmap.ic_eth).getIntrinsicHeight();
        
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        list = (ListView)findViewById(R.id.list);
        adViewMain = (AdView)findViewById(R.id.ad_view_main);
        
        setSupportActionBar(toolbar);
    
        preferences = new CoinPushPreferences(this);
        preferencesEditor = preferences.edit();
        
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    
        // Print preferences
        //Map<String, ?> allEntries = preferences.getAll();
        //for (Map.Entry<String, ?> entry : allEntries.entrySet())
        //    Log.d("foo", entry.getKey() + ": " + entry.getValue().toString());
        
        if(preferences.getBoolean(getString(R.string.key_preference_ads), false))
            enableAds();
        
        conversions = new ConversionList(preferences.getString(getString(R.string.key_preference_conversions), null));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(parent.getContext(), ActivityPreferencesConversion.class);
                intent.putExtra(getString(R.string.key_intent_conversions_index), position);
                startActivity(intent);
            }
        });
        conversionAdapter = new ConversionAdapter(this, conversions);
        list.setAdapter(conversionAdapter);
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        
        databaseReferenceConversionData = database.getReference("conversionData");
        databaseReferenceConversionDataTimestamp = databaseReferenceConversionData.child("timestamp");
        
        user = auth.getCurrentUser();
        if(user == null)
        {
            auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        user = auth.getCurrentUser();
                        databaseReferenceUser = database.getReference("users/" + user.getUid());
                        databaseReferenceUser.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                        syncPreferences();
                    }
                    else
                    {
                        // do stuff that should be done
                    }
                }
            });
        }
        else
        {
            databaseReferenceUser = database.getReference("users").child(user.getUid());
            databaseReferenceUser.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
            syncPreferences();
        }
        
        conversions.addListeners();
        databaseReferenceConversionDataTimestamp.addValueEventListener(eventListener);
    }
    
    @Override
    public void onStop()
    {
        conversions.removeListeners();
        databaseReferenceConversionDataTimestamp.removeEventListener(eventListener);
        
        super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*// Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            return true;
        }
        
        return super.onOptionsItemSelected(item);*/
        
        switch(item.getItemId())
        {
        case R.id.action_add_currency:
            new FragmentAddConversion().show(getFragmentManager(), "FOO");
            return true;
        case R.id.action_settings:
            startActivityForResult(new Intent(this, ActivityPreferencesGlobal.class), getResources().getInteger(R.integer.request_preferences_global));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == getResources().getInteger(R.integer.request_preferences_global))
        {
            String keyAds = getString(R.string.key_preference_ads);
            
            if(data.hasExtra(keyAds))
            {
                if(data.getBooleanExtra(keyAds, false))
                    enableAds();
                else
                    disableAds();
            }
        }
    }
    
    void enableAds()
    {
        if(mobileAdsUninitialized)
        {
            MobileAds.initialize(this, APP_ID);
            mobileAdsUninitialized = false;
            adRequestMain = new AdRequest.Builder().build();
            adRequestPrefsConversion = new AdRequest.Builder().build();
        }
        adViewMain.loadAd(adRequestMain);
        adViewMain.setVisibility(View.VISIBLE);
    }
    
    void disableAds()
    {
        adViewMain.setVisibility(View.GONE);
    }
    
    void syncPreferences()
    {
        if(preferencesSynced)
            return;
        databaseReferenceUser.child("conversionPrefs").removeValue();
        DatabaseReference databaseReferenceConvernversionPrefs = databaseReferenceUser.child("conversionPrefs");
        for(Conversion conversion : conversions)
        {
            DatabaseReference prefs = databaseReferenceConvernversionPrefs.child(conversion.getKeyString());
            prefs.child("pushIncreased")
                .setValue(preferences.getBoolean(conversion, R.string.key_preference_push_enabled_increased));
            prefs.child("pushDecreased")
                .setValue(preferences.getBoolean(conversion, R.string.key_preference_push_enabled_decreased));
            prefs.child("thresholdIncreased")
                .setValue(preferences.getFloat(conversion, R.string.key_preference_push_threshold_increase));
            prefs.child("thresholdDecreased")
                .setValue(preferences.getFloat(conversion, R.string.key_preference_push_threshold_decrease));
        }
        preferencesSynced = true;
    }
}
