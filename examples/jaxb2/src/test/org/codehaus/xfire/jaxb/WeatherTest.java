package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.jdom.Document;
import org.springframework.context.ApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

public class WeatherTest 
    extends AbstractXFireSpringTest
{
    public void testService() throws Exception
    {
        Document response = invokeService("WeatherService", "GetWeatherByZip.xml");

        addNamespace("w", "http://www.webservicex.net");
        assertValid("//s:Body/w:GetWeatherByZipCodeResponse/w:GetWeatherByZipCodeResult", response);
    }
    
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
           "/org/codehaus/xfire/spring/xfire.xml",
           "/org/codehaus/xfire/jaxb/beans.xml"
        });
    }
}
