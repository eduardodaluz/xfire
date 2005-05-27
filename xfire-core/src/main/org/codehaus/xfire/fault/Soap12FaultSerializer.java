package org.codehaus.xfire.fault;

import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Soap12FaultSerializer
    implements MessageSerializer
{
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        throw new UnsupportedOperationException("Reading faults is currently unsupported.");
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        XFireFault fault = (XFireFault) message.getBody();
        try
        {
            Map namespaces = fault.getNamespaces();
            for (Iterator itr = namespaces.keySet().iterator(); itr.hasNext();)
            {
                String prefix = (String) itr.next();
                writer.writeAttribute("xmlns:" + prefix, (String) namespaces.get(prefix));
            }

            writer.writeStartElement("soap:Fault");

            writer.writeStartElement("soap:Code");

            writer.writeStartElement("soap:Value");
            writer.writeCharacters("soap:" + fault.getFaultCode());
            writer.writeEndElement(); // Value

            if (fault.getSubCode() != null)
            {
                writer.writeStartElement("soap:SubCode");
                writer.writeStartElement("soap:Value");
                writer.writeCharacters(fault.getSubCode());
                writer.writeEndElement(); // Value
                writer.writeEndElement(); // SubCode
            }

            writer.writeEndElement(); // Code

            writer.writeStartElement("soap:Reason");
            writer.writeStartElement("soap:Text");
            writer.writeCharacters(fault.getReason());
            writer.writeEndElement(); // Text
            writer.writeEndElement(); // Reason

            if (fault.getRole() != null)
            {
                writer.writeStartElement("soap:Role");
                writer.writeCharacters(fault.getRole());
                writer.writeEndElement();
            }

            if (fault.hasDetails())
            {
                Node details = fault.getDetail();

                writer.writeStartElement("soap:Detail");

                NodeList children = details.getChildNodes();
                for (int i = 0; i < children.getLength(); i++)
                {
                    Node n = children.item(i);
                    if (n instanceof Element)
                    {
                        STAXUtils.writeElement((Element) n, writer);
                    }
                }

                writer.writeEndElement(); // Details
            }

            writer.writeEndElement(); // Fault
        }
        catch (XMLStreamException xe)
        {
            throw new XFireRuntimeException("Couldn't create fault.", xe);
        }
    }
}
