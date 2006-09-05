package org.codehaus.xfire.generator.oneway;

import jsr181.jaxb.oneway.SendMessageClient;
import jsr181.jaxb.oneway.SendMessagePortType;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;

public class SendMessageTest extends AbstractXFireAegisTest
{
    private Service service;
    private SendMessageImpl server;
    
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        AnnotationServiceFactory asf = new AnnotationServiceFactory(new Jsr181WebAnnotations(),
                                                                    getXFire().getTransportManager(),
                                                                    new AegisBindingProvider(new JaxbTypeRegistry()));
        service = asf.create(SendMessageImpl.class);
        server = new SendMessageImpl();
        service.setInvoker(new BeanInvoker(server));
        
        getServiceRegistry().register(service);
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        getServiceRegistry().unregister(service);
        super.tearDown();
    }
    
    @Override
    protected XFire getXFire()
    {
        return XFireFactory.newInstance().getXFire();
    }
    
    public void testEcho() throws Exception
    {   
        SendMessageClient stub = new SendMessageClient();
        
        SendMessagePortType client = stub.getSendMessagePortTypeLocalEndpoint();

        assertNotNull(client);
        
        client.sendMessage("foo");
        
        Thread.sleep(1000);
        
        assertEquals("foo", server.getMessage());
    }
}
