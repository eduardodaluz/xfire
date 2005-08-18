package org.codehaus.xfire.soap.handler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;

public class ReadHeadersHandler
    extends AbstractHandler
{
    public String getPhase()
    {
        return Phase.PARSE;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        InMessage message = context.getInMessage();
        if (message.getHeader() != null) return;
        
        XMLStreamReader reader = message.getXMLStreamReader();

        boolean end = !reader.hasNext();
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
                    return;
                case XMLStreamReader.END_ELEMENT:
                    break;
                case XMLStreamReader.START_ELEMENT:
                    if (reader.getLocalName().equals("Header"))
                    {
                        readHeaders(context);
                    }
                    else if (reader.getLocalName().equals("Body"))
                    {
                        seekToWhitespaceEnd(reader);

                        checkForFault(context, message, reader);

                        return;
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

    protected void checkForFault(MessageContext context, InMessage msg, XMLStreamReader reader) 
        throws XFireFault
    {
        if (reader.getEventType() == XMLStreamReader.START_ELEMENT)
        {
            if (reader.getName().equals(msg.getSoapVersion().getFault()))
            {
                MessageSerializer serializer = context.getService().getFaultSerializer();
                
                serializer.readMessage(msg, context);
                
                throw (XFireFault) msg.getBody();
            }
        }
    }

    private void seekToWhitespaceEnd(XMLStreamReader reader)
        throws XMLStreamException
    {
        int event = reader.next();
        if (event != XMLStreamReader.SPACE) return;
        
        do
        {
            event = reader.next();
        }
        while (event == XMLStreamReader.SPACE);
        
        return;
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
}
