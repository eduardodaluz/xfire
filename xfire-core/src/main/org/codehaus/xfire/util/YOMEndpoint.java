package org.codehaus.xfire.util;

import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;

public class YOMEndpoint
    implements ChannelEndpoint
{
    private int count = 0;
    private Document message;
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        StaxBuilder builder = new StaxBuilder();
        try
        {
            Element root = builder.buildElement(null, msg.getXMLStreamReader());
            if (root != null)
            {
                message = new Document(root);
            }
            else
            {
                message = null;
            }
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
        count++;
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