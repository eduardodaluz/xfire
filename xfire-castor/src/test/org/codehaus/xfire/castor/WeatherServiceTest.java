package org.codehaus.xfire.castor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class WeatherServiceTest
    extends AbstractXFireTest
{
    private Service endpoint;

    private ObjectServiceFactory builder;

    public void setUp()
        throws Exception
    {
        super.setUp();
        // XXX set to stax-dev input factory as it was setting to woodstox input
        // factory that was throwing a ClassCastException
        System.setProperty("javax.xml.stream.XMLInputFactory",
                             "com.bea.xml.stream.MXParserFactory");

        CastorTypeMappingRegistry registry = new CastorTypeMappingRegistry();
        builder = new ObjectServiceFactory(getXFire().getTransportManager(),
                new AegisBindingProvider(registry));
        ArrayList schemas = new ArrayList();
        schemas.add(getTestFile("src/test-schemas/WeatherForecast.xsd").getAbsolutePath());
        Map props = new HashMap();
        props.put(ObjectServiceFactory.SCHEMAS, schemas);
        
        builder.setStyle(SoapConstants.STYLE_DOCUMENT);

        endpoint = builder.create(WeatherServiceImpl.class,
                                  "WeatherService",
                                  "urn:WeatherService",
                                  props);

        getServiceRegistry().register(endpoint);
    }

    public void testService()
        throws Exception
    {
        MessagePartInfo info = (MessagePartInfo) endpoint.getServiceInfo()
                .getOperation("GetWeatherByZipCode").getInputMessage().getMessageParts().get(0);

        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//s:Body/w:GetWeatherByZipCodeResponse/w:GetWeatherByZipCodeResult", response);
    }

    public void testWsdl()
        throws Exception
    {
        Document doc = getWSDLDocument("WeatherService");

        addNamespace("xsd", SoapConstants.XSD);

        assertValid("//xsd:schema[@targetNamespace='http://www.webservicex.net']", doc);
    }

}
