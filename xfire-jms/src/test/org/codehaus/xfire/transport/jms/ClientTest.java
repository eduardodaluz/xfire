package org.codehaus.xfire.transport.jms;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ClientTest
        extends AbstractXFireJMSTest
{
    private Service service;

    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory factory = (ObjectServiceFactory) getServiceFactory();
        factory.addSoap11Transport(JMSTransport.BINDING_ID);
        
        service = factory.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        JMSChannel serverChannel = (JMSChannel) getTransport().createChannel("jms://Echo");
 
        Client client = new Client(getTransport(), service, "jms://Echo", "jms://Peer1");

        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        String resString = (String) response[0];
        assertEquals("hello", resString);

        serverChannel.close();
    }
}
