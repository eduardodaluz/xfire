package org.codehaus.xfire.jaxb2;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WeatherServiceTest
        extends AbstractXFireTest
{
    private Service endpoint;
    private ObjectServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new JaxbServiceFactory(getXFire().getTransportManager());
        builder.setStyle(SoapConstants.STYLE_DOCUMENT);

        endpoint = builder.create(WeatherService.class,
                                  "WeatherService",
                                  "urn:WeatherService",
                                  null);
        
        getServiceRegistry().register(endpoint);
    }

    public void testService()
            throws Exception
    {
        MessagePartInfo info = (MessagePartInfo)
            endpoint.getServiceInfo().getOperation("GetWeatherByZipCode").getInputMessage().getMessageParts().get(0);

        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//s:Body/w:GetWeatherByZipCodeResponse/w:GetWeatherByZipCodeResult", response);
    }
    
}
