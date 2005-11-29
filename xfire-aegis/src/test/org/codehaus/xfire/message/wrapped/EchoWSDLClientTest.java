package org.codehaus.xfire.message.wrapped;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoWSDLClientTest
        extends AbstractXFireAegisTest
{
    private Service service;

    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class, "Echo", "urn:Echo", null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);
    }

    protected void tearDown()
        throws Exception
    {
        getServiceRegistry().unregister(service);
        
        super.tearDown();
    }

    protected XFire getXFire()
    {
        return XFireFactory.newInstance().getXFire();
    }

    public void testInvoke()
            throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        getWSDL("Echo").write(bos);

        WSDLServiceBuilder builder = new WSDLServiceBuilder(new ByteArrayInputStream(bos.toByteArray()));
        builder.setTransportManager(getTransportManager());
        builder.walkTree();
        
        Service service = (Service) builder.getServices().iterator().next();
        assertTrue(service.getBindingProvider() instanceof AegisBindingProvider);
        SoapBinding binding = (SoapBinding) service.getBindings().iterator().next();
        
        Client client = new Client(binding, "xfire.local://" + service.getSimpleName());
        client.setXFire(getXFire());
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");

        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        assertEquals("hello", response[0]);
    }
    
    public void testHTTPInvoke() throws Exception
    {
        XFireHttpServer server = new XFireHttpServer();
        server.setPort(8080);
        server.start();
        
        Client client = new Client(new URL("http://localhost:8080/Echo?wsdl"));
        
        OperationInfo op = client.getService().getServiceInfo().getOperation("echo");

        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        server.stop();
    }
}