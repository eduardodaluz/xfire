package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.jdom.Element;

public class XFireServerTest
    extends AbstractXFireTest
{
    private Service service;
    private XFireHttpServer server;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(EchoImpl.class);
        service.getBinding().setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);

        server = new XFireHttpServer();
        server.setPort(8391);
        server.start();
    }

    protected XFire getXFire()
    {
        XFireFactory factory = XFireFactory.newInstance();
        return factory.getXFire();
    }

    protected void tearDown()
        throws Exception
    {
        server.stop();
        
        super.tearDown();
    }

    public void testInvoke()
            throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Transport transport = getXFire().getTransportManager().getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);

        Client client = new Client(transport, service, "http://localhost:8391/EchoImpl");

        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];

        assertEquals(root.getName(), e.getName());
    }
}
