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

import android.support.annotation.DrawableRes;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

class Currency
{
    enum Code
    {
        ETH("ETH"), BTC("BTC"), LTC("LTC"), DASH("DASH"), XMR("XMR"), NXT("NXT"), ZEC("ZEC"), DGB("DGB"), XRP("XRP"),
        ETC("ETC"), BCH("BCH"), DOGE("DOGE"),
        USD("USD"), EUR("EUR"), GBP("GBP"), JPY("JPY"), CNY("CNY"), AUD("AUD"), CAD("CAD"), CHF("CHF"), DNT("DNT");
        private final String code;
        Code(String code) { this.code = code; }
        public String toString() { return code; }
    }
    
    private final static String STRING_REPLACE_SHOULD_HAVE_A_NON_REGEX_VERSION = "$";
    
    private final static String BASE_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=%s&tsyms=%s";
    private final static NumberFormat valueFormat;
    private final static String formatSymbol;
    
    final static HashMap<Code, Currency> currencies;
    final static ArrayList<Currency> currencyListFrom, currencyListTo;
    
    static
    {
        String bitcoinSymbol = android.os.Build.VERSION.SDK_INT < 26 ? "Ƀ" : "\u20BF";
        
        currencies = new HashMap<>();
        currencies.put(Code.ETH,  new Currency(Code.ETH,  "Ethereum", "Ξ", R.mipmap.ic_eth));
        currencies.put(Code.BTC,  new Currency(Code.BTC,  "Bitcoin", bitcoinSymbol, R.mipmap.ic_btc));
        currencies.put(Code.LTC,  new Currency(Code.LTC,  "Litecoin", "Ł", R.mipmap.ic_ltc));
        currencies.put(Code.DASH, new Currency(Code.DASH, "DigitalCash", "DASH", R.mipmap.ic_dash));
        currencies.put(Code.XMR,  new Currency(Code.XMR,  "Monero", "ɱ", R.mipmap.ic_xmr));
        currencies.put(Code.NXT,  new Currency(Code.NXT,  "Nxt", "NXT", R.mipmap.ic_nxt));
        currencies.put(Code.ZEC,  new Currency(Code.ZEC,  "ZCash", "ZEC", R.mipmap.ic_zec));
        currencies.put(Code.DGB,  new Currency(Code.DGB,  "DigiByte", "", R.mipmap.ic_dgb));
        currencies.put(Code.XRP,  new Currency(Code.XRP,  "Ripple", "", R.mipmap.ic_xrp));
        currencies.put(Code.BCH,  new Currency(Code.BCH,  "Bitcoin Cash", bitcoinSymbol, R.mipmap.ic_bch));
        currencies.put(Code.ETC,  new Currency(Code.ETC,  "Ethereum Classic", "", R.mipmap.ic_etc));
        currencies.put(Code.DOGE, new Currency(Code.DOGE, "Dogecoin", "Ð", R.mipmap.ic_doge));
        currencies.put(Code.DNT,  new Currency(Code.DNT,  "district0x", "DNT", R.mipmap.ic_dnt));
        
        currencies.put(Code.USD,  new Currency(Code.USD,  "US Dollar", "$", "\uD83C\uDDFA\uD83C\uDDF8"));
        currencies.put(Code.EUR,  new Currency(Code.EUR,  "Euro", "€", "\uD83C\uDDEA\uD83C\uDDFA"));
        currencies.put(Code.JPY,  new Currency(Code.JPY,  "Japanese Yen", "¥", "\uD83C\uDDEF\uD83C\uDDF5"));
        currencies.put(Code.GBP,  new Currency(Code.GBP,  "Pound Sterling", "£", "\uD83C\uDDEC\uD83C\uDDE7"));
        currencies.put(Code.CNY,  new Currency(Code.CNY,  "Chinese Yuan", "¥", "\uD83C\uDDE8\uD83C\uDDF3"));
        currencies.put(Code.AUD,  new Currency(Code.AUD,  "Australian Dollar", "$", "\uD83C\uDDE6\uD83C\uDDFA"));
        currencies.put(Code.CAD,  new Currency(Code.CAD,  "Canadian Dollar", "$", "\uD83C\uDDE8\uD83C\uDDE6"));
        currencies.put(Code.CHF,  new Currency(Code.CHF,  "Swiss Franc", "Fr", "\uD83C\uDDE8\uD83C\uDDED"));
        
        currencyListFrom = new ArrayList<>();
        currencyListFrom.add(currencies.get(Code.BTC));
        currencyListFrom.add(currencies.get(Code.ETH));
        currencyListFrom.add(currencies.get(Code.BCH));
        currencyListFrom.add(currencies.get(Code.LTC));
        currencyListFrom.add(currencies.get(Code.ETC));
        currencyListFrom.add(currencies.get(Code.DASH));
        currencyListFrom.add(currencies.get(Code.XMR));
        currencyListFrom.add(currencies.get(Code.ZEC));
        currencyListFrom.add(currencies.get(Code.DOGE));
        currencyListFrom.add(currencies.get(Code.NXT));
        currencyListFrom.add(currencies.get(Code.DGB));
        currencyListFrom.add(currencies.get(Code.XRP));
        currencyListFrom.add(currencies.get(Code.DNT));
        
        currencyListTo = new ArrayList<>();
        currencyListTo.add(currencies.get(Code.USD));
        currencyListTo.add(currencies.get(Code.EUR));
        currencyListTo.add(currencies.get(Code.JPY));
        currencyListTo.add(currencies.get(Code.GBP));
        currencyListTo.add(currencies.get(Code.AUD));
        currencyListTo.add(currencies.get(Code.CAD));
        currencyListTo.add(currencies.get(Code.CHF));
        currencyListTo.add(currencies.get(Code.CNY));
        currencyListTo.addAll(currencyListFrom);
        
        valueFormat = NumberFormat.getCurrencyInstance();
        valueFormat.setCurrency(java.util.Currency.getInstance("EUR"));
        valueFormat.setMaximumFractionDigits(16);
        formatSymbol = valueFormat.getCurrency().getSymbol();
    }
    
    final Code code;
    final String name;
    final String symbol;
    @DrawableRes final int icon;
    final String emoji;
    
    Currency(final Code code, final String name, final String symbol, @DrawableRes final int icon)
    {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
        emoji = "";
    }
    
    Currency(final Code code, final String name, final String symbol, final String emoji)
    {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        icon = R.mipmap.ic_empty;
        this.emoji = emoji;
    }
    
    Currency(final Code code, final String name, final String symbol)
    {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        icon = R.mipmap.ic_empty;
        emoji = "";
    }
    
    public String toString(final boolean includeCode)
    {
        if(includeCode)
            return name + " (" + code.toString() + ")";
        else
            return name;
    }
    
    public String toString()
    {
        return toString(false);
    }
    
    String getValueStr(final double value, final boolean includeCode)
    {
        String valueStr = valueFormat.format(value);
        
        if(symbol.equals(STRING_REPLACE_SHOULD_HAVE_A_NON_REGEX_VERSION))
            valueStr = valueStr.replaceFirst(formatSymbol, "\\$");
        else
            valueStr = valueStr.replaceFirst(formatSymbol, symbol);
        
        if(includeCode)
            valueStr += " " + code;
        
        return valueStr;
    }
    
    String getValueStr(final double value)
    {
        return getValueStr(value, false);
    }
}
