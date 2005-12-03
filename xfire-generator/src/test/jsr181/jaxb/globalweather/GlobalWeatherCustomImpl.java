
package jsr181.jaxb.globalweather;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import net.webservicex.GetCitiesByCountryResponse;
import net.webservicex.GetWeatherResponse;

@WebService(endpointInterface = "jsr181.jaxb.globalweather.GlobalWeatherSoap", serviceName = "GlobalWeather", targetNamespace = "http://www.webserviceX.NET")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public class GlobalWeatherCustomImpl
    implements GlobalWeatherSoap
{


    public GetCitiesByCountryResponse GetCitiesByCountry(net.webservicex.GetCitiesByCountry GetCitiesByCountry) {
        throw new UnsupportedOperationException();
    }

    public GetWeatherResponse GetWeather(net.webservicex.GetWeather GetWeather) {
        GetWeatherResponse response = new GetWeatherResponse();
        response.setGetWeatherResult("foo");
        return response;
    }

}
