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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;

/**
 * Created by mqduck on 7/11/17.
 */

public class ActivityPreferencesGlobal extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setPreferenceRefreshTitle(getString(R.string.key_preference_refresh_delay));
        setResult(getResources().getInteger(R.integer.request_preferences_global), getIntent());
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if( key.equals(getString(R.string.key_preference_refresh_delay)) )
        {
            setPreferenceRefreshTitle(key);
            getIntent().putExtra( key, Integer.valueOf(sharedPreferences.getString(key, "")) );
        }
        else if( key.equals(getString(R.string.key_preference_ads)) )
            getIntent().putExtra(key, sharedPreferences.getBoolean(key, false));
    }
    
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    
    private void setPreferenceRefreshTitle(String key)
    {
        ListPreference preferenceRefresh = (ListPreference)findPreference(key);
        preferenceRefresh.setTitle("Refresh every " + preferenceRefresh.getEntry());
    }
}
