package org.codehaus.xfire.type.java5;

public class CurrencyService
{
    public enum Currency
    {
        USD,
        POUNDS,
        EURO
    }
    
    public int convert(int input, Currency inputCurrency, Currency outputCurrency)
    {
        return input;
    }
}
