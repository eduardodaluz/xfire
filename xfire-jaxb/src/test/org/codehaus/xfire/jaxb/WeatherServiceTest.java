package org.codehaus.xfire.jaxb;

import java.util.ArrayList;

import net.webservicex.ObjectFactory;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl11.builder.DefaultWSDLBuilderFactory;
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

        ObjectFactory objectFactory = new ObjectFactory();
        builder = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                           new AegisBindingProvider(new JaxbTypeRegistry(objectFactory)));
        ArrayList schemas = new ArrayList();
        schemas.add("src/test-schemas/WeatherForecast.xsd");

        builder.setWsdlBuilderFactory(new DefaultWSDLBuilderFactory(schemas));
        builder.setStyle(SoapConstants.STYLE_DOCUMENT);

        endpoint = builder.create(WeatherServiceImpl.class,
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

    public void testWsdl() throws Exception
    {
        Document doc = getWSDLDocument("WeatherService");
        
        addNamespace("xsd", SoapConstants.XSD);
        
        assertValid("//xsd:schema[@targetNamespace='http://www.webservicex.net']", doc);
    }

}
