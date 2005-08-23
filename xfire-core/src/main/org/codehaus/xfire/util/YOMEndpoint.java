package org.codehaus.xfire.util;

import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.yom.Document;
import org.codehaus.yom.stax.StaxBuilder;

public class YOMEndpoint
    implements ChannelEndpoint
{
    private int count = 0;
    private Document message;
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        count++;
        StaxBuilder builder = new StaxBuilder();
        try
        {
            message = new Document(builder.buildElement(null, msg.getXMLStreamReader()));
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }

    public int getCount()
    {
        return count;
    }

    public Document getMessage()
    {
        return message;
    }
}