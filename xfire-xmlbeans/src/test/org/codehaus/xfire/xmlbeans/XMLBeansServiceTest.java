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

    public void testAnyService() throws Exception
    {
        Service any = builder.create(TestService.class, "TestService", "urn:TestService", null);
        getServiceRegistry().register(any);

        try
        {
            getWSDLDocument("TestService");
            assertTrue("Generating WSDL above should not throw an NPE", true);
        }
        catch (NullPointerException e)
        {
            fail("Shouldn't be throwing an NPE here");
        }
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
        //printNode(wsdl);
        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

	    assertValid("//wsdl:types/xsd:schema[@targetNamespace='http://www.webservicex.net']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='http://www.webservicex.net']" +
                "/xsd:element[@name='WeatherForecasts']", wsdl);
        assertValid("count(//xsd:schema[@targetNamespace='http://www.webservicex.net']" +
                    "/xsd:element[@name='WeatherForecasts'])=1", wsdl);
        assertValid("//xsd:schema[@targetNamespace='http://www.webservicex.net']" +
                "/xsd:complexType[@name='WeatherForecasts']", wsdl);
	}
    
    public void testAnyWSDL()
		throws Exception
	{
        builder = new XmlBeansServiceFactory(getXFire().getTransportManager());

        endpoint = builder.create(TestService.class,
                                  "TestService",
                                  "urn:TestService",
                                  null);
        getServiceRegistry().register(endpoint);
        
	    Document wsdl = getWSDLDocument("TestService");

        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

	    assertValid("//wsdl:types/xsd:schema[@targetNamespace='http://codehaus.org/xfire/xmlbeans']" +
                    "/xsd:element[@name='request']", wsdl);
	}

    public void testAnyWSDLNoDupRootRefElements()
		throws Exception
	{
        builder = new XmlBeansServiceFactory(getXFire().getTransportManager());

        endpoint = builder.create(TestService.class,
                                  "TestService",
                                  "urn:TestService",
                                  null);
        getServiceRegistry().register(endpoint);

	    Document wsdl = getWSDLDocument("TestService");

        String xpath_string="/wsdl:definitions/wsdl:types//xsd:schema/xsd:element[@name='trouble']";
        
        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );
        addNamespace( "s", SoapConstants.XSD );

        assertEquals(1, assertValid(xpath_string, wsdl).size());
	}

    public void testAnyWSDLNoDupRootElementNameElements()
		throws Exception
	{
        builder = new XmlBeansServiceFactory(getXFire().getTransportManager());

        endpoint = builder.create(TestService.class,
                                  "TestService",
                                  "urn:TestService",
                                  null);
        getServiceRegistry().register(endpoint);
	    Document wsdl = getWSDLDocument("TestService");

        String xpath_string="/wsdl:definitions/wsdl:types//xsd:schema/xsd:element[@name='trouble']";
        
        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );
        addNamespace( "s", SoapConstants.XSD );

        assertEquals(1, assertValid(xpath_string, wsdl).size());
	}

}
