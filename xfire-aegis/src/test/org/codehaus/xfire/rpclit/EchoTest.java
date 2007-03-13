package org.codehaus.xfire.rpclit;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.services.Echo;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class EchoTest
    extends AbstractXFireAegisTest
{
    private Service service;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.setStyle(SoapConstants.STYLE_RPC);
        service = osf.create(Echo.class, null, "urn:xfire:echo", null);

        getServiceRegistry().register(service);
    }

    public void testService() throws Exception
    {
        Document response = invokeService("Echo", "echo.xml");

        addNamespace("e", "urn:xfire:echo");
        
        assertValid("//out[text()='Yo Yo']", response);
        
        response = invokeService("Echo", "echo-nil.xml");
        assertValid("//out[@xsi:nil='true']", response);
    }
    
    public void testWSDL() throws Exception
    {
        getWSDLDocument(service.getSimpleName());

    }
}
