package org.codehaus.xfire.fault;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Node;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultSerializerTest
        extends AbstractXFireTest
{
    public void testFaults()
            throws Exception
    {
        Soap12FaultSerializer soap12 = new Soap12FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");
        fault.setSubCode("m:NotAvailable");
        Element e = new Element("t:bah", "urn:test");
        e.appendChild("bleh");
        fault.getDetail().appendChild(e);

        e = new Element("t:bah2", "urn:test2");
        e.appendChild("bleh");
        fault.getDetail().appendChild(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8");
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap12.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap12.getInstance().getNamespace());
        soap12.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        
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
        Soap11FaultSerializer soap11 = new Soap11FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");

        Node details = fault.getDetail();
        Element e = new Element("t:bah", "urn:test");
        e.appendChild("bleh");
        fault.getDetail().appendChild(e);

        e = new Element("t:bah2", "urn:test2");
        e.appendChild("bleh");
        fault.getDetail().appendChild(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8");
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap11.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap11.getInstance().getNamespace());
        soap11.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();

        Document doc = readDocument(out.toString());
        printNode(doc);
        addNamespace("s", Soap12.getInstance().getNamespace());
        addNamespace("t", "urn:test2");
        assertValid("//detail/t:bah2[text()='bleh']", doc);
        assertValid("//faultactor[text()='http://someuri']", doc);
    }
}
