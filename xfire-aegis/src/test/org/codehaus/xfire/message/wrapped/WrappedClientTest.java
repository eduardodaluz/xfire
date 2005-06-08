package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.local.LocalTransport;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedClientTest
        extends AbstractXFireAegisTest
{
    private Service service;
    private Service clientService;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceFactory factory = getServiceFactory();
        
        service = factory.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        clientService = factory.create(Echo.class);

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        LocalTransport transport = new LocalTransport();
        Channel channel = transport.createChannel(service);
        Client client = new Client(transport, clientService, channel.getUri());
        
        OperationInfo op = clientService.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {"hello"});
        assertNotNull("response from client invoke is null", response);
        assertEquals("unexpected array size in invoke response", 1, response.length);
        
        String resString = (String) response[0];
        assertEquals("unexpected response string", "hello", resString);
    }
}
