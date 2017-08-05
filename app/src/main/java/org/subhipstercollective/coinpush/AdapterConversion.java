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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by subhipstercollective on 7/4/17.
 */

class AdapterConversion extends ArrayAdapter<Conversion>
{
    //private final Context context;
    private final ConversionList conversions;
    private LayoutInflater inflater;
    
    AdapterConversion(final Context context, final ConversionList conversions)
    {
        super(context, -1, conversions);
        //this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.conversions = conversions;
    }
    
    @NonNull
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
    {
        View conversionView = convertView == null ? inflater.inflate(R.layout.conversion, parent, false) : convertView;
        TextView textCurrencyFrom = (TextView)conversionView.findViewById(R.id.textViewCurrencyFrom);
        TextView textValue = (TextView)conversionView.findViewById(R.id.textViewValue);
        TextView textChange = (TextView)conversionView.findViewById(R.id.textViewChange);
        ImageView iconFrom = (ImageView)conversionView.findViewById(R.id.icon_from);
        TextView emojiFrom = (TextView)conversionView.findViewById(R.id.emoji_from);
        
        Conversion conversion = conversions.get(position);
        textCurrencyFrom.setText(conversion.currencyFrom.toString(true));
        textValue.setText(conversion.currencyTo.getValueStr(conversion.getValue(), true));
        iconFrom.setImageResource(conversion.currencyFrom.icon);
        emojiFrom.setTextSize(TypedValue.COMPLEX_UNIT_PX, ActivityMain.emojiSize);
        emojiFrom.setText(conversion.currencyFrom.emoji);
        textChange.setTextColor(conversion.getChangeColor());
        textChange.setText( String.format(getContext().getString(R.string.text_conversion_change),
                conversion.getChange()) );
        
        return conversionView;
    }
    
    ConversionList getConversions() { return conversions; }
}
