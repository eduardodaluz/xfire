package org.codehaus.xfire.soap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.stax.StaxSerializer;

/**
 * Processes SOAP invocations. 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 28, 2004
 */
public class SoapSerializer
    implements MessageSerializer
{
    private static final String HANDLER_STACK = "xfire.handlerStack";

    public SoapSerializer()
    {
    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
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
                        if (reader.getLocalName().equals("Header"))
                        {
                            readHeaders(context);
                        }
                        else if (reader.getLocalName().equals("Body"))
                        {
                            context.getService().getBinding().readMessage(message, context);
                        }
                        else if (reader.getLocalName().equals("Envelope"))
                        {
                            message.setSoapVersion(reader.getNamespaceURI());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse message.", e, XFireFault.SENDER);
        }
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
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
    
            context.getService().getBinding().writeMessage(message, writer, context);
    
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

    /**
     * Read in the headers as a YOM Element and create a response Header.
     *
     * @param context
     * @throws XMLStreamException
     */
    protected void readHeaders(MessageContext context)
            throws XMLStreamException
    {
        StaxBuilder builder = new StaxBuilder();

        Element header = builder.buildElement(null, context.getInMessage().getXMLStreamReader());

        context.getInMessage().setHeader(header);
    }

    protected void createResponseHeader(MessageContext context)
    {
        QName headerQ = context.getOutMessage().getSoapVersion().getHeader();
        Element response = new Element(headerQ.getPrefix() + ":" + headerQ.getLocalPart(),
                headerQ.getNamespaceURI());

        context.getOutMessage().setHeader(response);
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
