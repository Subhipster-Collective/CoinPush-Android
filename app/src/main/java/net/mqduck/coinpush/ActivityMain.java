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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ActivityMain extends AppCompatActivity
{
    private final static String APP_ID = "ca-app-pub-9926113995373020~9860570594";
    private final static String AD_UNIT_ID_MAIN = "ca-app-pub-9926113995373020/3674436196";
    private final static String AD_UNIT_ID_CONVERSION = "ca-app-pub-9926113995373020/8551960990";
    
    private static int updateDelay;
    
    static ConversionList conversions;// = new ConversionList();
    static ConversionAdapter conversionAdapter;
    static float emojiSize;
    static SharedPreferences preferences;
    static SharedPreferences.Editor preferencesEditor;
    static Runnable updateRunnable;
    static Handler updateHandler;
    static AdView adViewMain, adViewPreferencesConversion;
    static boolean mobileAdsUninitialized = true;
    
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        emojiSize = (float)0.7 * getResources().getDrawable(R.mipmap.ic_eth).getIntrinsicHeight();
        
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ListView list = (ListView)findViewById(R.id.list);
        final FrameLayout adFrameMain = (FrameLayout)findViewById(R.id.ad_frame_main);
        
        setSupportActionBar(toolbar);
    
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesEditor = preferences.edit();
        
        if(preferences.getBoolean(getString(R.string.key_preference_ads), false))
            enableAds();
        
        conversions = new ConversionList(preferences.getString(getString(R.string.key_preference_conversions), null));
        updateDelay = Integer.valueOf(preferences.getString(getString(R.string.key_preference_refresh_delay),
                                                            getString(R.string.refresh_delay_default)));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Conversion conversion = conversions.get(position);
                Intent intent = new Intent(parent.getContext(), ActivityPreferencesConversion.class);
                intent.putExtra(getString(R.string.key_intent_conversions_index), position);
                startActivity(intent);
            }
        });
        conversionAdapter = new ConversionAdapter(this, conversions);
        list.setAdapter(conversionAdapter);
    
        updateRunnable = new Runnable() {
            @Override public void run()
            {
                updateDataThread();
                updateHandler.postDelayed(this, updateDelay);
            }
        };
        updateHandler = new Handler();
        updateHandler.post(updateRunnable);
        
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
        case R.id.action_refresh:
            updateData();
            return true;
        case R.id.action_settings:
            //startActivity(new Intent(this, ActivityPreferencesGlobal.class));
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
            String keyDelay = getString(R.string.key_preference_refresh_delay);
            String keyAds = getString(R.string.key_preference_ads);
            
            if(data.hasExtra(keyDelay))
                setUpdateDelay(data.getIntExtra(keyDelay, -1));
            if(data.hasExtra(keyAds))
            {
                if(data.getBooleanExtra(keyAds, false))
                    enableAds();
                else
                    disableAds();
            }
        }
    }
    
    private static void updateDataThread()
    {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... params)
            {
                Currency.updateJsons();
                for(Conversion conversion : conversions)
                    conversion.update();
                return null;
            }
            @Override protected void onPostExecute(Void result) { conversionAdapter.notifyDataSetChanged(); }
        }.execute();
    }
    
    static void updateData()
    {
        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.post(updateRunnable);
    }
    
    static void setUpdateDelay(final int updateDelay)
    {
        ActivityMain.updateDelay = updateDelay;
        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.postDelayed(updateRunnable, updateDelay);
    }
    
    void enableAds()
    {
        if(mobileAdsUninitialized)
        {
            MobileAds.initialize(this, APP_ID);
            mobileAdsUninitialized = false;
        }
        
        adViewMain = new AdView(this);
        adViewMain.setAdSize(AdSize.SMART_BANNER);
        adViewMain.setAdUnitId(AD_UNIT_ID_MAIN);
        adViewMain.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                FrameLayout.LayoutParams.WRAP_CONTENT));
        adViewMain.loadAd(new AdRequest.Builder().addTestDevice("B3AAAD21FB73238814182BF44E0B18FC").build());
        
        adViewPreferencesConversion = new AdView(this);
        adViewPreferencesConversion.setAdSize(AdSize.SMART_BANNER);
        adViewPreferencesConversion.setAdUnitId(AD_UNIT_ID_CONVERSION);
        adViewPreferencesConversion.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                FrameLayout.LayoutParams.WRAP_CONTENT));
        adViewPreferencesConversion.loadAd(new AdRequest.Builder().addTestDevice("B3AAAD21FB73238814182BF44E0B18FC")
                                                                  .build());
        
        ((FrameLayout)findViewById(R.id.ad_frame_main)).addView(adViewMain);
    }
    
    void disableAds()
    {
        ((FrameLayout)findViewById(R.id.ad_frame_main)).removeView(adViewMain);
        adViewMain = null;
        adViewPreferencesConversion = null;
    }
}
