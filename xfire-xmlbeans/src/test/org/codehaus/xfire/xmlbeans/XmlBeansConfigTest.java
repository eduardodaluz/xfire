package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.XMLServiceBuilder;
import org.jdom.Document;

public class XmlBeansConfigTest
    extends AbstractXFireTest
{
    public void testServicesXml() throws Exception
    {
        XMLServiceBuilder builder = new XMLServiceBuilder(getXFire());
        
        builder.buildServices(getResourceAsStream("services.xml"));
        
        assertTrue(getServiceRegistry().hasService("WeatherService"));
        
        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//w:GetWeatherByZipCodeResponse", response);
    }
}
