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

import java.util.ArrayList;

class ConversionList extends ArrayList<Conversion>
{
    final private static String DELIMITER_CONVERSION = ";";
    final private static String DELIMITER_CURRENCY = ":";
    
    ConversionList()
    {
        super();
    }
    
    ConversionList(final String conversionsString)
    {
        super();
        if(conversionsString != null && !conversionsString.isEmpty())
            for(String conStr : conversionsString.split(DELIMITER_CONVERSION))
            {
                String[] currencyCodes = conStr.split(DELIMITER_CURRENCY);
                add(new Conversion(currencyCodes[0], currencyCodes[1]));
            }
    }
    
    String getConverionsString()
    {
        String conversionString = "";
        for(Conversion conversion : this)
            conversionString += conversion.currencyFrom.code.toString()
                                + DELIMITER_CURRENCY
                                + conversion.currencyTo.code.toString()
                                + DELIMITER_CONVERSION;
        return conversionString;
    }
    
    public boolean add(Conversion conversion)
    {
        return !contains(conversion) && super.add(conversion);
    }
    
    public Conversion get(final Currency currencyFrom, final Currency currencyTo) // Throw exception when not found?
    {
        Conversion result = null;
        for(Conversion conversion : this)
            if(conversion.currencyFrom == currencyFrom && conversion.currencyTo == currencyTo)
            {
                result = conversion;
                break;
            }
        return result;
    }
    
    public Conversion get(final Currency.Code codeFrom, final Currency.Code codeTo)
    {
        return get(Currency.currencies.get(codeFrom), Currency.currencies.get(codeTo));
    }
    
    public Conversion get(final String codeStrFrom, final String codeStrTo)
    {
        return get(Currency.Code.valueOf(codeStrFrom), Currency.Code.valueOf(codeStrTo));
    }
    
    public boolean contains(final Currency currencyFrom, final Currency currencyTo)
    {
        for(Conversion conversion : this)
            if(conversion.currencyFrom == currencyFrom && conversion.currencyTo == currencyTo)
                return true;
        return false;
    }
    
    public boolean contains(final Conversion conversion)
    {
        return contains(conversion.currencyFrom, conversion.currencyTo);
    }
    
    public boolean contains(final Currency.Code codeFrom, Currency.Code codeTo)
    {
        return contains(Currency.currencies.get(codeFrom), Currency.currencies.get(codeTo));
    }
    
    public boolean contains(final String codeStrFrom, final String codeStrTo)
    {
        return contains(Currency.Code.valueOf(codeStrFrom), Currency.Code.valueOf(codeStrTo));
    }
    
    public void addListeners()
    {
        for(final Conversion conversion : this)
            conversion.addListener();
    }
    
    public void removeListeners()
    {
        for(final Conversion conversion : this)
            conversion.removeListener();
    }
}
