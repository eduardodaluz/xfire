package org.codehaus.xfire.castor;

import net.webservicex.*;

public class WeatherServiceImpl
{

    public GetWeatherByPlaceNameResponse DontCallThisMethod(GetWeatherByPlaceName body)
    {
        throw new RuntimeException("Dont call me!");
    }

    public GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body)
    {
        GetWeatherByZipCodeResponse res = new GetWeatherByZipCodeResponse();
        String zipCode = body.getZipCode();
        if (!zipCode.equals("1050"))
            throw new RuntimeException("Parameter isnt passed correctly. expected: 1050, got "
                    + zipCode);
        GetWeatherByZipCodeResult weather = new GetWeatherByZipCodeResult();

        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Vienna, AT");
        weather.setAllocationFactor(1);

        res.setGetWeatherByZipCodeResult(weather);

        return res;
    }

}
