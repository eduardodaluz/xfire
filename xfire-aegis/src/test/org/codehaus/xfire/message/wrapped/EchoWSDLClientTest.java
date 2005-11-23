package org.codehaus.xfire.message.wrapped;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.Client;
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
    public void setUp() throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(Echo.class, "Echo", "urn:Echo", null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);
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
        
        Client client = new Client(binding, "xfire.local://" + service.getName());
        client.setXFire(getXFire());
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");

        Object[] response = client.invoke(op, new Object[] {"hello"});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        assertEquals("hello", response[0]);
    }
}