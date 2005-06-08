package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.test.AbstractXFireTest;

public class XFireServerTest
    extends AbstractXFireTest
{
    private Service service;
    private Service clientService;
    private XFireHttpServer server;
    
    public void setUp() throws Exception
    {
        super.setUp();

        server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
        
        service = getServiceFactory().create(EchoImpl.class);
        service.getBinding().setBindingProvider(new MessageBindingProvider());

        clientService = getServiceFactory().create(EchoImpl.class);
        clientService.getBinding().setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);
    }

    
    protected void tearDown()
        throws Exception
    {
        server.stop();
        
        super.tearDown();
    }


    public void testInvoke()
            throws Exception
    {/*
        Element root = new Element("a:root", "urn:a");
        root.appendChild("hello");
        
        SoapHttpTransport transport =
            (SoapHttpTransport) getXFire().getTransportManager().getTransport(SoapHttpTransport.NAME);
        Channel channel = transport.createChannel(service);

        Client client = new Client(transport, clientService, "http://localhost:8191//Echo");
        
        OperationInfo op = clientService.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        assertEquals(root.getLocalName(), e.getLocalName());*/
    }
}
