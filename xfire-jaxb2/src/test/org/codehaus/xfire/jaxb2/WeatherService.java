package org.codehaus.xfire.jaxb2;

import net.webservicex.GetWeatherByZipCode;
import net.webservicex.GetWeatherByZipCodeResponse;
import net.webservicex.WeatherForecasts;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WeatherService
{
    public GetWeatherByZipCodeResponse GetWeatherByZipCode( GetWeatherByZipCode body )
    {
        GetWeatherByZipCodeResponse res = new GetWeatherByZipCodeResponse();
        
        WeatherForecasts weather = new WeatherForecasts();

        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Grand Rapids, MI");
        
        res.setGetWeatherByZipCodeResult(weather);
        
        return res;
    }
}
