package org.codehaus.xfire.xmlbeans;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap11;
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
    private XMLBeansServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new XMLBeansServiceFactory(getXFire().getTransportManager());

        endpoint = builder.create(WeatherService.class,
                                  "WeatherService",
                                  "urn:WeatherService",
                                  Soap11.getInstance(),
                                  SoapConstants.STYLE_DOCUMENT,
                                  SoapConstants.USE_LITERAL, null);
        
        getServiceRegistry().register(endpoint);
        
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        org.w3c.dom.Document schema = builder.parse(getTestFile("src/test-schemas/WeatherForecast.xsd"));
        
        endpoint.setWSDLWriter(new XMLBeansWSDLBuilder(endpoint, 
                                                       getXFire().getTransportManager().getTransports("WeatherService"),
                                                       schema));
    }

    public void testService()
            throws Exception
    {
        assertEquals(1, endpoint.getServiceInfo().getOperations().size());

        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//w:GetWeatherByZipCodeResponse", response);
        printNode(response);
    }
    
    public void testWSDL() 
		throws Exception
	{
	    Document wsdl = getWSDLDocument("WeatherService");
        
        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

	    assertValid("//wsdl:types/xsd:schema[@targetNamespace='http://www.webservicex.net']", wsdl);
	}
}
