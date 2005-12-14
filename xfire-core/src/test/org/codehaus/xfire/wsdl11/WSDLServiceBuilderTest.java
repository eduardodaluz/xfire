package org.codehaus.xfire.wsdl11;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLServiceBuilderTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echo.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        QName name = service.getName();
        assertNotNull(name);
        assertEquals(new QName("urn:Echo", "Echo"), name);
        
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        OperationInfo opInfo = (OperationInfo) operations.iterator().next();
        assertEquals("echo", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        assertNotNull(message);
        
        Collection parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echoRequest"), part.getName());
        
        // Check the output message
        message = opInfo.getOutputMessage();
        assertNotNull(message);
        
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echoResponse"), part.getName());
        
        // Is the SOAP binding stuff around?
        AbstractSoapBinding soapOp = (AbstractSoapBinding) service.getBindings().iterator().next();
        assertNotNull(soapOp);
        assertEquals("literal", soapOp.getUse());
        assertEquals("", soapOp.getSoapAction(opInfo));
        
        Collection endpoints = service.getEndpoints();
        assertEquals(1, endpoints.size());
        
        Endpoint endpoint = (Endpoint) endpoints.iterator().next();
        assertEquals(new QName("urn:Echo", "EchoHttpPort"), endpoint.getName());
        assertNotNull(endpoint.getBinding());
        assertEquals("http://localhost:8080/xfire/services/Echo", endpoint.getUrl());
    }
    
    public void testUnknownBinding()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoHttp.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        QName name = service.getName();
        assertNotNull(name);
        assertEquals(new QName("urn:Echo", "Echo"), name);
        
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
    }
    
    public void testSimpleVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoSimple.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        QName name = service.getName();
        assertNotNull(name);
        assertEquals(new QName("urn:Echo", "Echo"), name);
        
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        OperationInfo opInfo = (OperationInfo) operations.iterator().next();
        assertEquals("echo", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        assertNotNull(message);
        
        Collection parts = message.getMessageParts();
        assertEquals(1, parts.size());
    }
}
