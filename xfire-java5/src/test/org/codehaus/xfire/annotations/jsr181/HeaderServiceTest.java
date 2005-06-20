package org.codehaus.xfire.annotations.jsr181;


import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class HeaderServiceTest
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
        AnnotationServiceFactory osf = new AnnotationServiceFactory(new Jsr181WebAnnotations(),
                                                                    getXFire().getTransportManager(),
                                                                    null);

        Service service = osf.create(HeaderService.class, "HeaderService", "urn:HeaderService", null);
        getXFire().getServiceRegistry().register(service);
        
        Document response = invokeService("HeaderService", "/org/codehaus/xfire/annotations/jsr181/headerMessage.xml");
        assertNotNull(HeaderService.a);
        assertEquals("one", HeaderService.a);
        assertNotNull(HeaderService.b);
        assertEquals("three", HeaderService.b);
        assertNotNull(HeaderService.header);
        assertEquals("two", HeaderService.header);
    }
    
    public static class HeaderService
    {
        static String a;
        static String b;
        static String header;
        
        @WebMethod
        public void doSomething(@WebParam(name="a") String a,
                                @WebParam(name="header", header=true) String header,
                                @WebParam(name="b") String b) 
        {
            HeaderService.a = a;
            HeaderService.b = b;
            HeaderService.header = header;
        }
    }
}