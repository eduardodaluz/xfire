package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.exchange.OutMessage;

public class OutMessageRequestEntity
    implements RequestEntity
{
    private OutMessage message = null;
    private MessageContext context;

    private static final Log log = LogFactory.getLog(OutMessageRequestEntity.class);
    
    public OutMessageRequestEntity(OutMessage msg,MessageContext context)
    {
        this.message = msg;
        this.context = context;
    }

    public boolean isRepeatable()
    {
        return true;
    }

    public void writeRequest(OutputStream out)
        throws IOException
    {
        try
        {
            Attachments atts = message.getAttachments();
            if (atts != null)
            {
                atts.write(out);
            }
            else
            {
                HttpChannel.writeWithoutAttachments(context, message, out);
            }
        }
        catch (XFireException e)
        {
            log.error("Couldn't send message.", e);
            throw new IOException(e.getMessage());
        }
    }

    public long getContentLength()
    {
        // not known so we send negative value
        return -1;
    }

    public String getContentType()
    {
        return HttpChannel.getSoapMimeType(message);
    }
}
