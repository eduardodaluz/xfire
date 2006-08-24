package org.codehaus.xfire.castor;

import net.webservicex.GetWeatherByPlaceName;
import net.webservicex.GetWeatherByPlaceNameResponse;
import net.webservicex.GetWeatherByZipCode;
import net.webservicex.GetWeatherByZipCodeResponse;

public interface WeatherService
{
    public abstract GetWeatherByPlaceNameResponse DontCallThisMethod(GetWeatherByPlaceName body);

    public abstract GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body);
}
