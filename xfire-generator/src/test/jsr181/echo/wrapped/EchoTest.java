package jsr181.echo.wrapped;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.xmlbeans.XmlBeansTypeRegistry;

public class EchoTest   
    extends AbstractXFireAegisTest
{
    private Service service;
    
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        AnnotationServiceFactory asf = new AnnotationServiceFactory(new Jsr181WebAnnotations(),
                                                                    getXFire().getTransportManager(),
                                                                    new AegisBindingProvider(new XmlBeansTypeRegistry()));
        service = asf.create(CustomEchoImpl.class);

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
        EchoClient service = new EchoClient();
        
        QName  ep = new QName("urn:echo:wrapped", "EchoLocalPort");
        EchoPortType echo = service.getEndpoint(ep);
        
        assertEquals("foo", echo.echo("foo"));
    }
}
