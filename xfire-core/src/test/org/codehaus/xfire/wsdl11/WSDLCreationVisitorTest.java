package org.codehaus.xfire.wsdl11;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.test.ServiceEndpoints;
import org.custommonkey.xmlunit.XMLTestCase;

public class WSDLCreationVisitorTest
        extends XMLTestCase
{
    private WSDLCreationVisitor wsdlCreationVisitor;
    private ServiceEndpoint endpoint;

    public void method()
    {

    }

    protected void setUp()
            throws Exception
    {
        wsdlCreationVisitor = new WSDLCreationVisitor();
        endpoint = ServiceEndpoints.getEchoFaultService();
    }

    public void testSOAPWsdl()
            throws Exception
    {
        endpoint.accept(wsdlCreationVisitor);
        Definition definition = wsdlCreationVisitor.getDefinition();
        assertNotNull(definition);
        PortType portType = definition.getPortType(endpoint.getService().getName());
        assertNotNull(portType);
        assertEquals(1, portType.getOperations().size());
        Operation operation = portType.getOperation("echo", "echoRequest", "echoResponse");
        assertNotNull(operation);
        assertNotNull(operation.getInput());
        assertNotNull(operation.getOutput());
        assertEquals(1, operation.getFaults().size());
        assertEquals(3, definition.getMessages().size());
        assertNotNull(definition.getMessage(new QName("echoRequest")));
        assertNotNull(definition.getMessage(new QName("echoResponse")));
        assertNotNull(definition.getMessage(new QName("echoFault")));
    }
}