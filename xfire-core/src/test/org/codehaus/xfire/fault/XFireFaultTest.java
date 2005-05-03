package org.codehaus.xfire.fault;

import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.BadHandler;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireFaultTest
        extends AbstractXFireTest
{
    public void testFaults()
            throws Exception
    {
        Soap12FaultHandler soap12 = new Soap12FaultHandler();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");
        fault.setSubCode("m:NotAvailable");
        Node details = fault.getDetail();
        Element e = details.getOwnerDocument().createElementNS("urn:test", "bah");
        DOMUtils.setText(e, "bleh");
        details.appendChild(e);

        e = details.getOwnerDocument().createElementNS("urn:test2", "bah2");
        DOMUtils.setText(e, "bleh");
        details.appendChild(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context =
                new MessageContext("Echo",
                                   null,
                                   out,
                                   null,
                                   null);

        soap12.handleFault(fault, context);

        Document doc = readDocument(out.toString());
        printNode(doc);
        addNamespace("s", Soap12.getInstance().getNamespace());
        assertValid("//s:SubCode/s:Value[text()='m:NotAvailable']", doc);
        addNamespace("t", "urn:test2");
        assertValid("//s:Detail/t:bah2[text()='bleh']", doc);
        assertValid("//s:Role[text()='http://someuri']", doc);
    }


    public void testFaults11()
            throws Exception
    {
        Soap11FaultHandler soap11 = new Soap11FaultHandler();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");

        Node details = fault.getDetail();
        Element e = details.getOwnerDocument().createElementNS("urn:test", "bah");
        DOMUtils.setText(e, "bleh");
        details.appendChild(e);

        e = details.getOwnerDocument().createElementNS("urn:test2", "bah2");
        DOMUtils.setText(e, "bleh");
        details.appendChild(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context =
                new MessageContext("Echo",
                                   null,
                                   out,
                                   null,
                                   null);

        soap11.handleFault(fault, context);
        System.err.println(out.toString());

        Document doc = readDocument(out.toString());
        printNode(doc);
        addNamespace("s", Soap12.getInstance().getNamespace());
        addNamespace("t", "urn:test2");
        assertValid("//detail/t:bah2[text()='bleh']", doc);
        assertValid("//faultactor[text()='http://someuri']", doc);
    }

    public void testSOAP12()
            throws Exception
    {
        Soap12FaultHandler soap12 = new Soap12FaultHandler();

        testHandler(soap12);
    }

    public void testSOAP11()
            throws Exception
    {
        Soap11FaultHandler soap11 = new Soap11FaultHandler();

        testHandler(soap11);
    }

    /**
     * @param soap11
     */
    private void testHandler(FaultHandler soap11)
    {
        ServiceInfo serviceInfo = new ServiceInfo(new QName("Echo"), getClass());
        SOAPBinding binding = SOAPBindingFactory.createDocumentBinding(new QName("EchoBinding"), Soap12.getInstance());
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo, binding);

        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        endpoint.setWSDLWriter(writer);

        endpoint.setServiceHandler(new BadHandler());
        endpoint.setFaultHandler(soap11);

        getServiceRegistry().register(endpoint);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context =
                new MessageContext("Echo",
                                   null,
                                   out,
                                   null,
                                   null);

        getXFire().invoke(getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                          context);

        System.out.println(out.toString());
    }
}
