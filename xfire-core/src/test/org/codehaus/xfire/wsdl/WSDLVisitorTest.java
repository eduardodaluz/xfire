package org.codehaus.xfire.wsdl;

import java.lang.reflect.Method;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;

public class WSDLVisitorTest
        extends TestCase
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

    public void testWsdl()
            throws Exception
    {
        endpoint.accept(wsdlVisitor);
        Definition definition = wsdlVisitor.getDefinition();
        WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
        writer.writeWSDL(definition, System.out);
    }


}