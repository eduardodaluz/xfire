package org.codehaus.xfire.jaxb;

import net.webservicex.GetWeatherByPlaceName;
import net.webservicex.GetWeatherByPlaceNameResponse;
import net.webservicex.GetWeatherByZipCode;
import net.webservicex.GetWeatherByZipCodeResponse;

/**
 * User: chris
 * Date: Aug 20, 2005
 * Time: 11:19:29 PM
 */
public interface WeatherService
{
    GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body);

    GetWeatherByPlaceNameResponse getWeatherByPlaceName(GetWeatherByPlaceName body);

    GetWeatherByPlaceNameResponse DontCallThisMethod(GetWeatherByPlaceName body);
}
