package org.codehaus.xfire.service.binding;


import java.lang.reflect.Method;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

public class HeaderBindingTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
    }

    public void testHeaders()
            throws Exception
    {
        ObjectServiceFactory osf = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                            new MessageBindingProvider())
        {
            protected boolean isHeader(Method method, int j)
            {
                return (j == 1);
            }
        };
        osf.setStyle(SoapConstants.STYLE_MESSAGE);
        
        Service service = osf.create(HeaderService.class, "HeaderService", "urn:HeaderService", null);
        getXFire().getServiceRegistry().register(service);
        
        Document response = invokeService("HeaderService", "/org/codehaus/xfire/service/binding/header.xml");
        assertNotNull(HeaderService.a);
        assertEquals("a", HeaderService.a.getLocalName());
        assertNotNull(HeaderService.b);
        assertEquals("b", HeaderService.b.getLocalName());
        assertNotNull(HeaderService.header);
        assertEquals("in1", HeaderService.header.getLocalName());
    }
    
    public static class HeaderService
    {
        static Element a;
        static Element b;
        static Element header;
        
        public void doSomething(Element a, Element header, Element b) 
        {
            HeaderService.a = a;
            HeaderService.b = b;
            HeaderService.header = header;
        }
    }
}