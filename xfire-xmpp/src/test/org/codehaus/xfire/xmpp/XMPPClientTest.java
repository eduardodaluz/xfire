package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPClientTest
        extends AbstractXFireAegisTest
{
    private Service service;

    private Transport clientTrans;
    private Transport serverTrans;

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
        // XMPPConnection.DEBUG_ENABLED = true;       

        clientTrans = SoapTransport.createSoapTransport(new XMPPTransport(getXFire(), server, "xfireTestClient", "password2"));
        serverTrans = SoapTransport.createSoapTransport(new XMPPTransport(getXFire(), server, username, password));
        
        getXFire().getTransportManager().register(serverTrans);
    }
    
    protected void tearDown()
        throws Exception
    {
        clientTrans.dispose();
        serverTrans.dispose();
        
        super.tearDown();
    }

    public void testInvoke()
            throws Exception
    {
        Channel serverChannel = serverTrans.createChannel("Echo");

        Client client = new Client(clientTrans, service, id + "/Echo");

        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        String resString = (String) response[0];
        assertEquals("hello", resString);
    }
}
