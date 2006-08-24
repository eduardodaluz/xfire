package org.codehaus.xfire.x582;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.jaxws.JAXWSServiceFactory;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.Soap11Binding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.jdom.Document;

public class XFire582Test extends AbstractXFireAegisTest
{

    private Service service;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        JAXWSServiceFactory asf = new JAXWSServiceFactory(getTransportManager());
        service = asf.create(CustomXFireNamespaceProblemServiceImpl.class,
                             null,
                             getTestFile("src/wsdl/XFire582.wsdl").toURL(),
                             null);
        
        Soap11Binding binding = new Soap11Binding(new QName("Soap"), 
                                                  LocalTransport.BINDING_ID, 
                                                  service);
        service.addBinding(binding);
        binding.setStyle(SoapConstants.STYLE_WRAPPED);
        binding.setSerializer(AbstractSoapBinding.getSerializer(SoapConstants.STYLE_WRAPPED,
                                                                SoapConstants.USE_LITERAL));
        Endpoint endpoint = new Endpoint(new QName("SoapEndpoint"), 
                                         binding, 
                                         "xfire.local://XFireNamespaceProblemService");
        service.addEndpoint(endpoint);
        
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
        assertValid("//b:makeCallResponse/b:status", res);
    }
}
