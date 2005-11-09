package org.codehaus.xfire.jaxb;

import javax.jws.WebMethod;
import javax.jws.WebService;

import net.webservicex.GetWeatherByZipCode;
import net.webservicex.GetWeatherByZipCodeResponse;

@WebService(name="WeatherServiceIntf", targetNamespace="http://www.webservicex.net")
public interface WeatherService
{
    @WebMethod
    GetWeatherByZipCodeResponse GetWeatherByZipCode(GetWeatherByZipCode body);
}
