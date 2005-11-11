package org.codehaus.xfire.wsdl11;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.DocumentBinding;
import org.codehaus.xfire.service.binding.WrappedBinding;
import org.codehaus.xfire.soap.SoapOperationInfo;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedWSDLTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoWrapped.wsdl"));
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        Iterator itr = operations.iterator();
        OperationInfo opInfo = (OperationInfo) itr.next();
        assertEquals("echo", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        Collection parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "text"), part.getName());
        
        // and now the output...
        message = opInfo.getOutputMessage();
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "text"), part.getName());

        assertTrue(service.getBinding() instanceof WrappedBinding);
        
        Collection endpoints = service.getServiceInfo().getEndpoints();
        assertEquals(1, endpoints.size());
        
        Endpoint endpoint = (Endpoint) endpoints.iterator().next();
        assertEquals(new QName("urn:Echo", "EchoHttpPort"), endpoint.getName());
        assertEquals("http://schemas.xmlsoap.org/soap/http", endpoint.getBindingId());
        assertEquals("http://localhost:8080/xfire/services/Echo", endpoint.getAddress());
    }
    
    public void testBadWrapped()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoBadWrapped.wsdl"));
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        Iterator itr = operations.iterator();
         /*
          * The echo2 operation shouldn't be wrapped since it has attributes
          */
        OperationInfo opInfo = (OperationInfo) itr.next();
        assertEquals("echo2", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        List parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echo2"), part.getName());
        
        // Check the output message
        message = opInfo.getOutputMessage();
        assertNotNull(message);
        
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echo2Response"), part.getName());
        
        // Is the SOAP binding stuff around?
        SoapOperationInfo soapOp = SoapOperationInfo.getSoapOperationInfo(opInfo);
        assertNotNull(soapOp);
        assertEquals("literal", soapOp.getUse());
        assertEquals("urn:Echo/echo2", soapOp.getSoapAction());
        
        assertTrue(service.getBinding() instanceof DocumentBinding);
    }
    
    public void testClient()
        throws Exception
    {
        //Client client = new Client(new URL("echo.wsdl"));
    }
}
