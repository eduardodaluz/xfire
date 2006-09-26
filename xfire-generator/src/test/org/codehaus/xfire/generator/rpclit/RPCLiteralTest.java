package org.codehaus.xfire.generator.rpclit;


import javax.jws.soap.SOAPBinding;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.util.LoggingHandler;
import org.codehaus.xfire.util.dom.DOMInHandler;

public class RPCLiteralTest
    extends AbstractXFireAegisTest
{
    private Service service;
    private EchoRpcLit server;
    
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        AnnotationServiceFactory asf = new JaxbServiceFactory(getXFire().getTransportManager());
        service = asf.create(EchoRpcLit.class);
        server = new EchoRpcLit();
        service.setInvoker(new BeanInvoker(server));
        service.addInHandler(new DOMInHandler());
        service.addInHandler(new LoggingHandler());
        
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
        SOAPBinding ann = EchoPortType.class.getAnnotation(SOAPBinding.class);
        assertEquals(SOAPBinding.Style.RPC, ann.style());
        
        EchoClient stub = new EchoClient();
        EchoPortType client = stub.getEchoPortTypeLocalEndpoint();
    
        assertNotNull(client);
        
        assertEquals("foo", client.echo("foo"));
    }
}
