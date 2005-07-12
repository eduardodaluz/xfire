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
import org.codehaus.yom.Element;

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
        server.setPort(8191);
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
        Element root = new Element("a:root", "urn:a");
        root.appendChild("hello");
        
        Transport transport = getXFire().getTransportManager().getTransport(SoapHttpTransport.NAME);

        Client client = new Client(transport, service, "http://localhost:8191/EchoImpl");

        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        System.out.println(e.toXML());
        assertEquals(root.getLocalName(), e.getLocalName());
    }
}
