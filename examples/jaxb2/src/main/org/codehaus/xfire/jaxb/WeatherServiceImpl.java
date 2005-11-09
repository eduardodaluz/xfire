package org.codehaus.xfire.jaxb;


import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import net.webservicex.GetWeatherByZipCode;
import net.webservicex.GetWeatherByZipCodeResponse;
import net.webservicex.WeatherForecastsType;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
@WebService(endpointInterface="org.codehaus.xfire.jaxb.WeatherService", serviceName="WeatherService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class WeatherServiceImpl implements WeatherService
{

    public GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body)
    {
        GetWeatherByZipCodeResponse res = new GetWeatherByZipCodeResponse();
        String zipCode = body.getZipCode();
        
        WeatherForecastsType weather = new WeatherForecastsType();

        weather.setLatitude(1);
        weather.setLongitude(1);
        weather.setPlaceName("Vienna, AT");
        weather.setAllocationFactor(1);

        res.setGetWeatherByZipCodeResult(weather);

        return res;
    }
}
