package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.fault.SOAP11FaultHandler;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.java.DefaultJavaService;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceTest
	extends AbstractXFireTest
{
    public void setUp() 
    	throws Exception
    {
        super.setUp();
        
		DefaultJavaService service = new DefaultJavaService();
		service.setName("WeatherService");
		service.setDefaultNamespace("urn:WeatherService");
		service.setServiceClass(WeatherService.class.getName());
		
		XMLBeansServiceHandler handler = new XMLBeansServiceHandler();
		SoapHandler sHandler = new SoapHandler(handler);
		service.setServiceHandler(sHandler);
		
		service.setFaultHandler(new SOAP11FaultHandler());
		
		getServiceRegistry().register(service);
    }
    
    public void testService() 
    	throws Exception
    {
        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");
        
        addNamespace("w", "http://www.webservicex.net");
        assertValid("//w:GetWeatherByZipCodeResponse", response);
    }
}
