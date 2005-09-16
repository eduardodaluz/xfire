package org.codehaus.xfire.jaxb;

import net.webservicex.impl.GetWeatherByZipCodeResponseImpl;
import net.webservicex.impl.WeatherForecastsImpl;
import net.webservicex.*;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WeatherServiceImpl
{

    public GetWeatherByPlaceNameResponse DontCallThisMethod(GetWeatherByPlaceName body)
    {
        throw new RuntimeException("Dont call me!");
    }

    public GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body)
    {
        GetWeatherByZipCodeResponse res = new GetWeatherByZipCodeResponseImpl();
        String zipCode = body.getZipCode();
        if (!zipCode.equals("1050"))
            throw new RuntimeException("Parameter isnt passed correctly. expected: 1050, got " + zipCode);
        WeatherForecasts weather = new WeatherForecastsImpl();

        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Vienna, AT");
        weather.setAllocationFactor(1);

        res.setGetWeatherByZipCodeResult(weather);

        return res;
    }

}
