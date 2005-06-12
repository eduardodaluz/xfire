package org.codehaus.xfire.fault;

import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.stax.StaxSerializer;

public class Soap12FaultSerializer
    implements MessageSerializer
{
    private StaxBuilder builder = new StaxBuilder();
    
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        XFireFault fault = new XFireFault();
    
        DepthXMLStreamReader reader = new DepthXMLStreamReader(message.getXMLStreamReader());
    
        try
        {
            boolean end = false;
            while (!end && reader.hasNext())
            {
                int event = reader.next();
                switch (event)
                {
                    case XMLStreamReader.START_DOCUMENT:
                        String encoding = reader.getCharacterEncodingScheme();
                        message.setEncoding(encoding);
                        break;
                    case XMLStreamReader.END_DOCUMENT:
                        end = true;
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        break;
                    case XMLStreamReader.START_ELEMENT:
                        if (reader.getLocalName().equals("Code"))
                        {
                            reader.next();
                            STAXUtils.toNextElement(reader);
                            
                            if (reader.getLocalName().equals("Value"))
                            {
                                fault.setFaultCode(parseFaultCode(reader, reader.getElementText()));
                            }
                        }
                        else if (reader.getLocalName().equals("SubCode"))
                        {
                            reader.next();
                            STAXUtils.toNextElement(reader);
                            
                            if (reader.getLocalName().equals("Value"))
                            {
                                fault.setSubCode(reader.getElementText());
                            }
                            
                        }
                        else if (reader.getLocalName().equals("Reason"))
                        {
                            reader.next();
                            STAXUtils.toNextElement(reader);
                            
                            if (reader.getLocalName().equals("Text"))
                            {
                                fault.setMessage(reader.getElementText());
                            }
                        }
                        else if (reader.getLocalName().equals("Actor"))
                        {
                            fault.setRole(reader.getElementText());
                        }
                        else if (reader.getLocalName().equals("Detail"))
                        {
                            fault.setDetail(builder.buildElement(null, reader));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not parse message.", e, XFireFault.SENDER);
        }
        message.setBody(fault);
    }
    
    protected static String parseFaultCode(XMLStreamReader reader, String value)
    {
        int colon = value.indexOf(":");
        if (colon > -1)
        {
            String prefix = value.substring(0, colon);
            String local = value.substring(colon+1);
            String uri = reader.getNamespaceURI(prefix);

            if (uri.equals(Soap12.getInstance().getNamespace()))
            {
                return local;
            }
            
            return value;
        }
        else
        {
            return value;
        }
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
                Element detail = fault.getDetail();

                writer.writeStartElement("soap:Detail");

                StaxSerializer serializer = new StaxSerializer();
                Elements details = detail.getChildElements();
                for (int i = 0; i < details.size(); i++)
                {
                    serializer.writeElement(details.get(i), writer);
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
