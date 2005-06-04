package org.codehaus.xfire.client;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.yom.Element;

public class XFireProxyTest
        extends AbstractXFireTest
{
    private String url;
    private XFireProxyFactory factory;
    private Service service;
    private Service clientService;
    private LocalTransport transport = new LocalTransport();
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        service.getBinding().setBindingProvider(new MessageBindingProvider());

        clientService = getServiceFactory().create(Echo.class);
        clientService.getBinding().setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);
        factory = new XFireProxyFactory();
        url = "http://localhost:8080/services/Echo";
    }


    public void testHandleEquals()
            throws Exception
    {
        Echo echoProxy1 = (Echo) factory.create(transport, clientService, "");

        assertEquals(echoProxy1, echoProxy1);
    }

    public void testHandleHashCode()
            throws Exception
    {
        Echo echoProxy = (Echo) factory.create(transport, clientService, "");
        
        assertTrue(echoProxy.hashCode() != 0);
    }
    
    public void testInvoke() throws Exception
    {
        Element root = new Element("a:root", "urn:a");
        root.appendChild("hello");
        
        Channel channel = transport.createChannel(service);
        
        XFireProxyFactory factory = new XFireProxyFactory();
        Echo echo = (Echo) factory.create(transport, clientService, channel.getUri());
        
        Element e = echo.echo(root);
        assertEquals(root.getLocalName(), e.getLocalName());
    }
}