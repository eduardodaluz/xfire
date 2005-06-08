package org.codehaus.xfire.client;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.yom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class LocalClientTest
        extends AbstractXFireTest
{
    private Service service;
    private Service clientService;
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class);
        service.getBinding().setBindingProvider(new MessageBindingProvider());

        clientService = getServiceFactory().create(Echo.class);
        clientService.getBinding().setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        Element root = new Element("a:root", "urn:a");
        root.appendChild("hello");
        
        LocalTransport transport = new LocalTransport();
        Channel channel = transport.createChannel(service);
        
        Client client = new Client(transport, clientService, channel.getUri());
        
        OperationInfo op = clientService.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        assertEquals(root.getLocalName(), e.getLocalName());
    }
/*
    public void testFault()
            throws Exception
    {
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, BadEcho.class);

        //invokeService("Echo", "/org/codehaus/xfire/echo11.xml");
        Element root = new Element("a:root", "urn:a");
        root.appendChild("hello");
        
        LocalTransport transport = new LocalTransport();
        Channel channel = transport.createChannel(service);
        
        Client client = new Client(transport, clientService, channel.getUri());
        
        OperationInfo op = clientService.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        assertEquals(root.getLocalName(), e.getLocalName());
    }
*/
}
