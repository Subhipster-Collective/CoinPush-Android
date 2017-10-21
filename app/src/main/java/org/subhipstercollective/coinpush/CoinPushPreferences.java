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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by mqduck on 7/31/17.
 */

public class CoinPushPreferences implements SharedPreferences
{
    private final static float DEFAULT_THRESHOLD = 30.0f;
    
    final private SharedPreferences preferences;
    final private Context context;
    
    @SuppressWarnings("UnusedReturnValue")
    class Editor implements SharedPreferences.Editor
    {
        final private SharedPreferences.Editor editor;
        
        @SuppressLint("CommitPrefEdits")
        private Editor()
        {
            editor = preferences.edit();
        }
    
        @Override
        public SharedPreferences.Editor putString(String s, @Nullable String s1)
        {
            return editor.putString(s, s1);
        }
    
        @Override
        public SharedPreferences.Editor putStringSet(String s, @Nullable Set<String> set)
        {
            return editor.putStringSet(s, set);
        }
    
        @Override
        public SharedPreferences.Editor putInt(String s, int i)
        {
            return editor.putInt(s, i);
        }
    
        @Override
        public SharedPreferences.Editor putLong(String s, long l)
        {
            return editor.putLong(s, l);
        }
    
        @Override
        public SharedPreferences.Editor putFloat(String s, float v)
        {
            return editor.putFloat(s, v);
        }
        
        public SharedPreferences.Editor putFloat(final Conversion conversion, final @StringRes int preferenceKey,
                                                 final String valueStr)
        {
            return putFloat(getPrefKeyStr(preferenceKey, conversion), Float.valueOf(valueStr));
        }
    
        @Override
        public SharedPreferences.Editor putBoolean(String s, boolean b)
        {
            return editor.putBoolean(s, b);
        }
        
        public SharedPreferences.Editor putBoolean(final Conversion conversion, final @StringRes int preferenceKey,
                                                   final boolean value)
        {
            return putBoolean(getPrefKeyStr(preferenceKey, conversion), value);
        }
    
        @Override
        public SharedPreferences.Editor remove(String s)
        {
            return editor.remove(s);
        }
        
        public SharedPreferences.Editor remove(final Conversion conversion, final @StringRes int preferenceKey)
        {
            return editor.remove(getPrefKeyStr(preferenceKey, conversion));
        }
    
        @Override
        public SharedPreferences.Editor clear()
        {
            return editor.clear();
        }
    
        @Override
        public boolean commit()
        {
            return editor.commit();
        }
    
        @Override
        public void apply()
        {
            editor.apply();
        }
    }
    
    CoinPushPreferences(final Context context)
    {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    @Override
    public Map<String, ?> getAll()
    {
        return preferences.getAll();
    }
    
    @Nullable
    @Override
    public String getString(String s, @Nullable String s1)
    {
        return preferences.getString(s, s1);
    }
    
    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set)
    {
        return preferences.getStringSet(s, set);
    }
    
    @Override
    public int getInt(String s, int i)
    {
        return preferences.getInt(s, i);
    }
    
    @Override
    public long getLong(String s, long l)
    {
        return preferences.getLong(s, l);
    }
    
    @Override
    public float getFloat(String s, float v)
    {
        return preferences.getFloat(s, v);
    }
    
    public float getFloat(final Conversion conversion, final @StringRes int preferenceKey, final float defValue)
    {
        return getFloat(getPrefKeyStr(preferenceKey, conversion), defValue);
    }
    
    public float getFloat(final Conversion conversion, final @StringRes int preferenceKey)
    {
        return getFloat(conversion, preferenceKey, DEFAULT_THRESHOLD);
    }
    
    public String getFloatStr(final Conversion conversion, final @StringRes int preferenceKey, final float defValue)
    {
        return String.format(Locale.getDefault(), "%.2f", getFloat(conversion, preferenceKey, defValue));
    }
    
    public String getFloatStr(final Conversion conversion, final @StringRes int preferenceKey)
    {
        return getFloatStr(conversion, preferenceKey, DEFAULT_THRESHOLD);
    }
    
    @Override
    public boolean getBoolean(String s, boolean b)
    {
        return preferences.getBoolean(s, b);
    }
    
    public boolean getBoolean(final Conversion conversion, final @StringRes int preferenceKey, final boolean defValue)
    {
        return getBoolean(getPrefKeyStr(preferenceKey, conversion), defValue);
    }
    
    public boolean getBoolean(final Conversion conversion, final @StringRes int preferenceKey)
    {
        return getBoolean(conversion, preferenceKey, false);
    }
    
    @Override
    public boolean contains(String s)
    {
        return preferences.contains(s);
    }
    
    @Override
    public Editor edit()
    {
        return new Editor();
    }
    
    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener)
    {
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
    
    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener)
    {
        preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
    
    String getPrefKeyStr(final @StringRes int preferenceKey, final Conversion conversion)
    {
        return context.getString(preferenceKey) + conversion.getKeyString();
    }
}
