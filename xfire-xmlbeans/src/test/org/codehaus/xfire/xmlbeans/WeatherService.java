package org.codehaus.xfire.xmlbeans;

import net.webservicex.GetWeatherByZipCodeDocument;
import net.webservicex.GetWeatherByZipCodeResponseDocument;
import net.webservicex.WeatherForecasts;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WeatherService
{
    public GetWeatherByZipCodeResponseDocument GetWeatherByZipCode( GetWeatherByZipCodeDocument body )
    {
        GetWeatherByZipCodeResponseDocument res =
            GetWeatherByZipCodeResponseDocument.Factory.newInstance();
        
        WeatherForecasts weather = 
            res.addNewGetWeatherByZipCodeResponse().addNewGetWeatherByZipCodeResult();
        
        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Grand Rapids, MI");
        
        return res;
    }
}
