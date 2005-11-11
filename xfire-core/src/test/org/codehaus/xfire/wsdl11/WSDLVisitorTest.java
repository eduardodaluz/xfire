package org.codehaus.xfire.wsdl11;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapOperationInfo;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLVisitorTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echo.wsdl"));
        builder.walkTree();
        
        Collection services = builder.getServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        QName name = service.getServiceInfo().getName();
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
        SoapOperationInfo soapOp = SoapOperationInfo.getSoapOperationInfo(opInfo);
        assertNotNull(soapOp);
        assertEquals("literal", soapOp.getUse());
        assertEquals("", soapOp.getSoapAction());
        
        Collection endpoints = service.getServiceInfo().getEndpoints();
        assertEquals(1, endpoints.size());
        
        Endpoint endpoint = (Endpoint) endpoints.iterator().next();
        assertEquals(new QName("urn:Echo", "EchoHttpPort"), endpoint.getName());
        assertEquals("http://schemas.xmlsoap.org/soap/http", endpoint.getBindingId());
        assertEquals("http://localhost:8080/xfire/services/Echo", endpoint.getAddress());
    }
    
    public void testClient()
        throws Exception
    {
        //Client client = new Client(new URL("echo.wsdl"));
    }
}
