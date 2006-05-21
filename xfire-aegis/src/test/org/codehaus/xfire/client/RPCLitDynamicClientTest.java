package org.codehaus.xfire.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.local.LocalTransport;

public class RPCLitDynamicClientTest
    extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory factory = (ObjectServiceFactory) getServiceFactory();
        factory.setStyle(SoapConstants.STYLE_RPC);
        
        service = factory.create(TestService.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, TestService.class);

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        getWSDL("TestService").write(bos);
 
        Client client = new Client(new ByteArrayInputStream(bos.toByteArray()), null);
        client.setXFire(getXFire());
        client.setUrl("xfire.local://TestService");
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        
        Object[] response = client.invoke("receive", new Object[] {new Integer(1), Boolean.FALSE, "hi", Boolean.TRUE});
        assertNotNull("response from client invoke is null", response);
        assertEquals("unexpected array size in invoke response", 1, response.length);
        
        Integer res = (Integer) response[0];
    }
    
    public static class TestService 
    {
        public int receive(int one, boolean two, String three, boolean four)
        {
            return one;
        }
    }
}
