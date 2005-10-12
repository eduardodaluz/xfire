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
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.binding.ObjectBinding;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.stax.StaxSerializer;

public class Soap11FaultSerializer
    implements MessageSerializer
{
    private StaxBuilder builder = new StaxBuilder();
    
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        XFireFault fault = new XFireFault();

        XMLStreamReader reader = message.getXMLStreamReader();

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
                        if (reader.getLocalName().equals("faultcode"))
                        {
                            fault.setFaultCode(reader.getElementText());
                        }
                        else if (reader.getLocalName().equals("faultstring"))
                        {
                            fault.setMessage(reader.getElementText());
                        }
                        else if (reader.getLocalName().equals("faultactor"))
                        {
                            fault.setRole(reader.getElementText());
                        }
                        else if (reader.getLocalName().equals("detail"))
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

            writer.writeStartElement("faultcode");

            String codeString = fault.getFaultCode();
            if (codeString.equals(XFireFault.RECEIVER))
            {
                codeString = "Server";
            }
            if (codeString.equals(XFireFault.SENDER))
            {
                codeString = "Server";
            }
            else if (codeString.equals(XFireFault.DATA_ENCODING_UNKNOWN))
            {
                codeString = "Client";
            }

            writer.writeCharacters(codeString);
            writer.writeEndElement();

            writer.writeStartElement("faultstring");
            writer.writeCharacters(fault.getMessage());
            writer.writeEndElement();

            Throwable cause = fault.getCause();
            OperationInfo op = context.getExchange().getOperation();
            MessagePartInfo faultPart = null;
            if (op != null)
            {
                faultPart = getFaultForClass(op, cause.getClass());
            }
            
            if (fault.hasDetails() || faultPart != null)
            {
                Element detail = fault.getDetail();

                writer.writeStartElement("detail");

                ObjectBinding binding = context.getService().getBinding();
                if (faultPart != null && binding != null)
                {
                    binding.getBindingProvider().writeParameter(faultPart, writer, context, cause);
                }
                
                StaxSerializer serializer = new StaxSerializer();
                Elements details = detail.getChildElements();
                for (int i = 0; i < details.size(); i++)
                {
                    serializer.writeElement(details.get(i), writer);
                }

                writer.writeEndElement(); // Details
            }

            if (fault.getRole() != null)
            {
                writer.writeStartElement("faultactor");
                writer.writeCharacters(fault.getRole());
                writer.writeEndElement();
            }

            writer.writeEndElement(); // Fault
        }
        catch (XMLStreamException xe)
        {
            throw new XFireRuntimeException("Couldn't create fault.", xe);
        }
    }
    

    public MessagePartInfo getFaultForClass(OperationInfo op, Class class1)
    {
        for (Iterator itr = op.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            MessagePartInfo info = (MessagePartInfo) faultInfo.getMessageParts().get(0);
            
            if (info.getTypeClass().equals(class1))
                return info;
        }
        
        return null;
    }
}
