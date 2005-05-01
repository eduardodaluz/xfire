package org.codehaus.xfire.wsdl;

import java.lang.reflect.Method;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.HTTPBinding;
import org.codehaus.xfire.service.transport.MockTransport;
import org.custommonkey.xmlunit.XMLTestCase;

public class WSDLVisitorTest
        extends XMLTestCase
{
    private WSDLVisitor wsdlVisitor;
    private ServiceEndpoint endpoint;

    public void method()
    {

    }

    protected void setUp()
            throws Exception
    {
        wsdlVisitor = new WSDLVisitor();
        ServiceInfo service = new ServiceInfo(new QName("service"), String.class);
        endpoint = new ServiceEndpoint(service);
        endpoint.setTransport(new MockTransport("address"));
        Method method = getClass().getMethod("method", new Class[0]);

        OperationInfo operation = service.addOperation("operation", method);
        MessageInfo inputMessage = operation.createMessage(new QName("input"));
        operation.setInputMessage(inputMessage);
        MessageInfo outputMessage = operation.createMessage(new QName("output"));
        operation.setOutputMessage(outputMessage);
        FaultInfo fault = operation.addFault("fault");
        inputMessage.addMessagePart(new QName("inputpart1"), String.class);
        inputMessage.addMessagePart(new QName("inputpart2"), String.class);
        outputMessage.addMessagePart(new QName("outputpart1"), String.class);
        outputMessage.addMessagePart(new QName("outputpart2"), String.class);
        fault.addMessagePart(new QName("faultpart1"), String.class);
        fault.addMessagePart(new QName("faultpart2"), String.class);

    }

    public void testSOAPWsdl()
            throws Exception
    {
        endpoint.accept(wsdlVisitor);
        Definition definition = wsdlVisitor.getDefinition();
        assertNotNull(definition);
        PortType portType = definition.getPortType(endpoint.getService().getName());
        assertNotNull(portType);
        assertEquals(1, portType.getOperations().size());
        Operation operation = portType.getOperation("operation", "input", "output");
        assertNotNull(operation);
        assertNotNull(operation.getInput());
        assertNotNull(operation.getOutput());
        assertEquals(1, operation.getFaults().size());
        assertEquals(3, definition.getMessages().size());
        assertNotNull(definition.getMessage(new QName("input")));
        assertNotNull(definition.getMessage(new QName("output")));
        assertNotNull(definition.getMessage(new QName("fault")));
        wsdlVisitor.write(System.out);
    }

    public void testHTTPWsdl()
            throws Exception
    {
        HTTPBinding httpBinding = new HTTPBinding(new QName("httpBinding"));
        endpoint.setBinding(httpBinding);
        endpoint.accept(wsdlVisitor);
        wsdlVisitor.write(System.out);
    }

}