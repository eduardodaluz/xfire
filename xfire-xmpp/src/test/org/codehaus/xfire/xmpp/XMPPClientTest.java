package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.transport.Channel;
import org.jivesoftware.smack.XMPPConnection;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPClientTest
        extends AbstractXFireAegisTest
{
    private Service service;
    private Service clientService;

    private XMPPTransport clientTrans;
    private XMPPTransport serverTrans;

    String username = "xfireTestServer";
    String password = "password1";
    String server = "bloodyxml.com";
    String id = username + "@" + server;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceFactory factory = getServiceFactory();

        service = factory.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        getServiceRegistry().register(service);
        XMPPConnection.DEBUG_ENABLED = true;       
        clientService = factory.create(Echo.class);

        clientTrans = new XMPPTransport(getServiceRegistry(), server, "xfireTestClient", "password2");
        serverTrans = new XMPPTransport(getServiceRegistry(), server, username, password);
        
        getXFire().getTransportManager().register(serverTrans);
    }

    public void testInvoke()
            throws Exception
    {
        Channel serverChannel = serverTrans.createChannel(service);

        Client client = new Client(clientTrans, clientService, id + "/Echo");

        OperationInfo op = clientService.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        String resString = (String) response[0];
        assertEquals("hello", resString);

        serverChannel.close();
    }
}
