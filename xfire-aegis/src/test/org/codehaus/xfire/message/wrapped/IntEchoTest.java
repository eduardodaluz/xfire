package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class IntEchoTest
        extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceFactory factory = getServiceFactory();
        
        service = factory.create(Echo.class);

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        Transport transport = getTransportManager().getTransport(LocalTransport.BINDING_ID);
        Client client = new Client(transport, service, "xfire.log://Echo");
        client.setXFire(getXFire());
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] { null });
        assertNotNull("response from client invoke is null", response);
        assertEquals("unexpected array size in invoke response", 1, response.length);
        
        assertNull(response[0]);
    }
    
    public static class Echo
    {
        public Integer echo(Integer i)
        {
            return i;
        }
    }
}
