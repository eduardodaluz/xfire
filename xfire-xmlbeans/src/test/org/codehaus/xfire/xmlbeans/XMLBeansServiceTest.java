package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.fault.SOAP11FaultHandler;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.java.JavaServiceHandler;
import org.codehaus.xfire.java.mapping.DefaultTypeMappingRegistry;
import org.codehaus.xfire.java.wsdl.JavaWSDLBuilder;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceTest
	extends AbstractXFireTest
{
    XMLBeansService service;
    
    public void setUp() 
    	throws Exception
    {
        super.setUp();
        
		service = new XMLBeansService();
		service.setName("WeatherService");
		service.setDefaultNamespace("urn:WeatherService");
		service.setServiceClass(WeatherService.class.getName());
		service.setUse(SOAPConstants.USE_LITERAL);
		service.setStyle(SOAPConstants.STYLE_DOCUMENT);
		
		JavaServiceHandler handler = new JavaServiceHandler();
		SoapHandler sHandler = new SoapHandler(handler);
		service.setServiceHandler(sHandler);
		
		service.setFaultHandler(new SOAP11FaultHandler());
		
		service.setWSDLBuilder(new JavaWSDLBuilder(getXFire().getTransportManager()));
		DefaultTypeMappingRegistry tr = new DefaultTypeMappingRegistry();
		service.setTypeMappingRegistry(tr);
		
		service.initializeTypeMapping();
		service.initializeOperations();
		
		getServiceRegistry().register(service);
    }
    
    public void testService() 
    	throws Exception
    {
        assertEquals(1, service.getOperations().size());
		assertNotNull( service.getTypeMapping() );
		
        
        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");
        
        addNamespace("w", "http://www.webservicex.net");
        assertValid("//w:GetWeatherByZipCodeResponse", response);
        printNode(response);
    }
    
    /*public void testWSDL() 
		throws Exception
	{
	    Document wsdl = getWSDLDocument("WeatherService");
	    printNode(wsdl);
	}*/
}
