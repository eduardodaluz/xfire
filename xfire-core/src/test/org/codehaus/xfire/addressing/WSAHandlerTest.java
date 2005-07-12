package org.codehaus.xfire.addressing;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class WSAHandlerTest
    extends AbstractXFireTest
{
    Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                                new MessageBindingProvider())
        {
            protected String getAction(OperationInfo op)
            {
                return "http://example.com/Echo";
            }
        };
        factory.setStyle(SoapConstants.STYLE_MESSAGE);
        
        ((DefaultXFire)getXFire()).addInHandler(new AddressingHandler());
        service = factory.create(EchoImpl.class, "Echo", "urn:Echo", null);
        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        Document response = invokeService(null, "/org/codehaus/xfire/addressing/echo.xml");
        
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }
}
