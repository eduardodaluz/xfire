package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTypeTest;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceTest
	extends AbstractXFireTypeTest
{
    private DefaultObjectService service;
    private XMLBeansServiceBuilder builder;
    
    public void setUp() 
    	throws Exception
    {
        super.setUp();
        
        builder = new XMLBeansServiceBuilder(getXFire().getTransportManager(), null);
        
        service = (DefaultObjectService) 
            builder.create(WeatherService.class,
                           "WeatherService",
                           "urn:WeatherService",
                           Soap11.getInstance(),
                           SoapConstants.STYLE_DOCUMENT,
                           SoapConstants.USE_LITERAL, null );

        getServiceRegistry().register( service );
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
