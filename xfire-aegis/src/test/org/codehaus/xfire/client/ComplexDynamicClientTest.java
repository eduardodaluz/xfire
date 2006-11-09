package org.codehaus.xfire.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.services.SimpleBean;
import org.codehaus.xfire.transport.local.LocalTransport;

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
        
        // -- From change 1929
        Service model = client.getService();
        AegisBindingProvider bp = (AegisBindingProvider) model.getBindingProvider();
        TypeMapping typeMapping = bp.getTypeMapping(model);
        
        BeanType bt = new BeanType();
        bt.setSchemaType(new QName("http://services.xfire.codehaus.org","SimpleBean"));
        bt.setTypeClass(SimpleBean.class);
        typeMapping.register(bt);
        // -- End insertion
        
        client.setXFire(getXFire());
        client.setUrl("xfire.local://BeanService");
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        
        Object[] response = client.invoke("getSimpleBean", new Object[] {});
        assertNotNull("response from client invoke is null", response);
        assertEquals("unexpected array size in invoke response", 1, response.length);
        
        SimpleBean res = (SimpleBean) response[0];
        assertEquals("bleh", res.getBleh());
    }
}
