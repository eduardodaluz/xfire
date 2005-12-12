package org.codehaus.xfire.transport.http;

import java.net.MalformedURLException;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.AsyncService;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.jdom.Element;

public class XFireServerTest
    extends AbstractXFireTest
{
    private Service service;
    private XFireHttpServer server;
    private Service asyncService;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.setVoidOneWay(true);
        
        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        service.setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);

        asyncService = getServiceFactory().create(AsyncService.class);
        
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
        
        Transport transport = getTransportManager().getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);

        Client client = new Client(transport, service, "http://localhost:8391/Echo");

        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});
        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];

        assertEquals(root.getName(), e.getName());
    }
    
    public void testAsync()
        throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");

        Transport transport = getTransportManager()
                .getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);

        Client client = new Client(transport, asyncService, "http://localhost:8391/AsyncService");

        Object[] response = client.invoke("echo", new Object[] { root });
        
        assertNull(response);
    }

    public void testProxy() throws MalformedURLException, XFireFault
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "http://localhost:8391/Echo");
        
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
}
