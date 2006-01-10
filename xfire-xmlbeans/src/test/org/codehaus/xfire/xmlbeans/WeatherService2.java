package org.codehaus.xfire.xmlbeans;

import net.webservicex.GetWeatherByZipCodeDocument;
import net.webservicex.GetWeatherByZipCodeResponseDocument;
import net.webservicex.WeatherForecasts;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WeatherService2
{
    public GetWeatherByZipCodeResponseDocument.GetWeatherByZipCodeResponse 
        GetWeatherByZipCode( GetWeatherByZipCodeDocument.GetWeatherByZipCode body )
    {
        GetWeatherByZipCodeResponseDocument.GetWeatherByZipCodeResponse res =
            GetWeatherByZipCodeResponseDocument.GetWeatherByZipCodeResponse.Factory.newInstance();
        
        WeatherForecasts weather = 
            res.addNewGetWeatherByZipCodeResult();
        
        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Grand Rapids, MI");
        
        return res;
    }
    
    public WeatherForecasts GetForecasts( )
    {
        WeatherForecasts weather = 
            WeatherForecasts.Factory.newInstance();
        
        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Grand Rapids, MI");
        
        return weather;
    }
}
