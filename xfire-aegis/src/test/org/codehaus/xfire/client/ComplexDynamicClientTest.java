package org.codehaus.xfire.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.w3c.dom.Document;

public class ComplexDynamicClientTest
    extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceFactory factory = getServiceFactory();
        
        service = factory.create(BeanService.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, BeanService.class);

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        getWSDL("BeanService").write(bos);
        
        Client client = new Client(new ByteArrayInputStream(bos.toByteArray()), null);
        client.setXFire(getXFire());
        client.setUrl("xfire.local://BeanService");
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        
        Object[] response = client.invoke("getSimpleBean", new Object[] {});
        assertNotNull("response from client invoke is null", response);
        assertEquals("unexpected array size in invoke response", 1, response.length);
        
        Document res = (Document) response[0];
    }
}
