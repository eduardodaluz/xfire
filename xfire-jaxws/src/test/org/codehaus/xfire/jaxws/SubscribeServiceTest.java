package org.codehaus.xfire.jaxws;

import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class SubscribeServiceTest extends AbstractXFireTest {

    private AnnotationServiceFactory osf;
    
    public void setUp()
            throws Exception
    {
        super.setUp();
        
        osf = new JAXWSServiceFactory(getXFire().getTransportManager());

        Service service = osf.create(SubscribeService.class);

        getXFire().getServiceRegistry().register(service);
    }

    public void testService()
            throws Exception
    {
        Document response = invokeService("SubscribeService", "/org/codehaus/xfire/jaxws/wsn-subscribe.xml");
        
        assertNotNull(response);
        assertNoFault(response);

        addNamespace("wsnt", "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd");
        assertValid("//wsnt:SubscribeResponse", response);
    }
    
    public void testWsdl()
    		throws Exception
	{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXFire().generateWSDL("SubscribeService", baos);
        System.out.println(baos.toString());
	}
    
}
