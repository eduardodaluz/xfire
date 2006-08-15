package org.codehaus.xfire.x582;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.jdom.Document;

public class XFire582Test extends AbstractXFireAegisTest
{

    private Service service;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        AnnotationServiceFactory asf = new AnnotationServiceFactory(getTransportManager());
        service = asf.create(CustomXFireNamespaceProblemServiceImpl.class);
        
        getServiceRegistry().register(service);
    }

    public void testService() throws Exception
    {
        OperationInfo operation = service.getServiceInfo().getOperation("makeCall");
        
        MessagePartInfo part = (MessagePartInfo) operation.getOutputMessage().getMessageParts().get(0);
        assertTrue(part.isSchemaElement());
        assertEquals("http://test.bt.com/2006/08/Service/Schema", part.getName().getNamespaceURI());
        
        Document res = invokeService("XFireNamespaceProblemService", "callmessage.xml");
        
        addNamespace("b", "http://test.bt.com/2006/08/Service/Schema");
        assertValid("//b:status", res);
    }
}
