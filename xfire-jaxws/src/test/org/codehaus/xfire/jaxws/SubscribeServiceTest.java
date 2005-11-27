package org.codehaus.xfire.jaxws;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class SubscribeServiceTest extends AbstractXFireTest {

    private AnnotationServiceFactory osf;
    
    public void setUp()
            throws Exception
    {
        super.setUp();
        
        osf = new AnnotationServiceFactory(new Jsr181WebAnnotations(),
                                           getXFire().getTransportManager(),
                                           new AegisBindingProvider(new JaxbTypeRegistry()));

        Service service = osf.create(SubscribeService.class);

        getXFire().getServiceRegistry().register(service);
    }

    public void testHeaders()
            throws Exception
    {
        Document response = invokeService("SubscribeService", "/org/codehaus/xfire/jaxws/wsn-subscribe.xml");
    }
    
}
