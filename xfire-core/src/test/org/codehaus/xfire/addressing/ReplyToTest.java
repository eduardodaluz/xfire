package org.codehaus.xfire.addressing;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.YOMEndpoint;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.yom.Document;

public class ReplyToTest
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
        
        ((DefaultXFire)getXFire()).addInHandler(new AddressingInHandler());
        ((DefaultXFire)getXFire()).addOutHandler(new AddressingOutHandler());
        
        service = factory.create(EchoImpl.class, "Echo", "urn:Echo", null);
        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        Transport t = getXFire().getTransportManager().getTransport(LocalTransport.NAME);
        Channel channel = t.createChannel("xfire.local://EchoReceiver");
        YOMEndpoint endpoint = new YOMEndpoint();
        channel.setEndpoint(endpoint);
        
        Document response = invokeService(null, "/org/codehaus/xfire/addressing/ReplyTo.xml");
        
        assertNull(response);
        Thread.sleep(1000);
        assertEquals(1, endpoint.getCount());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", endpoint.getMessage());
        
        addNamespace("wsa", WSAConstants.WSA_NAMESPACE_200502);
        assertXPathEquals("//s:Header/wsa:To", "xfire.local://EchoReceiver", endpoint.getMessage());
    }
}
