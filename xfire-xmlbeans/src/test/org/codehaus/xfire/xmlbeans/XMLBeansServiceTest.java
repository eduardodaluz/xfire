package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceTest
        extends AbstractXFireTest
{
    private Service endpoint;
    private XmlBeansServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new XmlBeansServiceFactory(getXFire().getTransportManager());

        endpoint = builder.create(WeatherService.class,
                                  "WeatherService",
                                  "urn:WeatherService",
                                  null);
        getServiceRegistry().register(endpoint);
    }

    public void testService()
            throws Exception
    {
        assertEquals(1, endpoint.getServiceInfo().getOperations().size());

        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//w:GetWeatherByZipCodeResponse", response);
    }
    
    public void testWSDL() 
		throws Exception
	{
	    Document wsdl = getWSDLDocument("WeatherService");
        printNode(wsdl);
        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

	    assertValid("//wsdl:types/xsd:schema[@targetNamespace='http://www.webservicex.net']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='urn:WeatherService']" +
                "/xsd:element[@ref='ns1:GetWeatherByZipCode']", wsdl);
	}
}
