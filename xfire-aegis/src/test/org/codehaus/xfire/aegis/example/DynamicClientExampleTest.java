package org.codehaus.xfire.aegis.example;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

public class DynamicClientExampleTest
    extends TestCase
{
    public void testCurrencyConverter() throws MalformedURLException, Exception
    {
        Client client = new Client(new URL("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL"));
        
        Object[] results = client.invoke("ConversionRate", new Object[] {"BRL", "UGX"});
        
        // Service returns a double of the currency conversion rate.
        System.out.println((Double) results[0]);
    }
    
    public void testWeatherForecast() throws MalformedURLException, Exception
    {
        Client client = new Client(new URL("http://www.webservicex.net/WeatherForecast.asmx?WSDL"));
        
        Object[] results = client.invoke("GetWeatherByZipCode", new Object[] {"49506"});
        
        // The response is a complex type which we don't know how to represent.
        // So XFire converts it into a org.w3c.dom.Document. You can then navigate
        // the document and find the desired information.
        DOMUtils.writeXml((Document) results[0], System.out);
    }
}
