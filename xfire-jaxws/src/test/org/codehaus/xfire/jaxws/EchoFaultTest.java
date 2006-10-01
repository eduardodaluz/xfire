package org.codehaus.xfire.jaxws;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.jdom.Document;

public class EchoFaultTest extends AbstractXFireAegisTest
{
    public void testEchoFault() throws Exception
    {
        JAXWSServiceFactory sf = new JAXWSServiceFactory(getTransportManager());
        
        Service service = sf.create(Echo.class);
        getServiceRegistry().register(service);
        
        Document res = invokeService(service.getSimpleName(), "echo.xml");
        
        addNamespace("x", "http://jaxws.xfire.codehaus.org");
        assertValid("//detail/x:EchoFault/x:someMessage", res);
    }
}
