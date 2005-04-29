package org.codehaus.xfire.fault;

import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.BadHandler;
import org.codehaus.xfire.service.DefaultService;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.DOMUtils;
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
        Service service = new DefaultService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));

        service.setServiceHandler(new BadHandler());
        service.setFaultHandler(soap11);

        getServiceRegistry().register(service);

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
