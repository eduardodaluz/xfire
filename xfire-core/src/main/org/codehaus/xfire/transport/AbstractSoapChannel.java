package org.codehaus.xfire.transport;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxSerializer;

public abstract class AbstractSoapChannel
    extends AbstractChannel
{    
    public void sendSoapMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            QName env = message.getSoapVersion().getEnvelope();
            writer.setPrefix(env.getPrefix(), env.getNamespaceURI());
            writer.writeStartElement(env.getPrefix(),
                                     env.getLocalPart(),
                                     env.getNamespaceURI());
            writer.writeNamespace(env.getPrefix(), env.getNamespaceURI());

            if (message.getHeader() != null && message.getHeader().getChildCount() > 0)
            {
                QName header = message.getSoapVersion().getHeader();
                writer.writeStartElement(header.getPrefix(),
                                         header.getLocalPart(),
                                         header.getNamespaceURI());
                
                writeHeaders(message, writer);
                
                writer.writeEndElement();
            }
            
            QName body = message.getSoapVersion().getBody();
            writer.writeStartElement(body.getPrefix(),
                                     body.getLocalPart(),
                                     body.getNamespaceURI());
    
            message.getSerializer().writeMessage(message, writer, context);
    
            writer.writeEndElement();
            writer.writeEndElement();
            
            writer.writeEndDocument();
    
            writer.close();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't write message.", e, XFireFault.RECEIVER);
        }
    }

    protected void writeHeaders(AbstractMessage msg, XMLStreamWriter writer)
            throws XMLStreamException
    {
        StaxSerializer ser = new StaxSerializer();

        Elements elements = msg.getHeader().getChildElements();
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            
            ser.writeElement(e, writer);
        }
    }
}
