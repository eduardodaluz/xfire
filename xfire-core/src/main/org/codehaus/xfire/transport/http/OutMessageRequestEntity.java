package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

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
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, getContentType());
        try
        {
            message.getSerializer().writeMessage(message, writer, context);
            
            writer.close();
        }
        catch (XFireFault e)
        {
            log.error("Couldn't send message.", e);
            throw new IOException(e.getMessage());
        }
        catch (XMLStreamException e)
        {
            log.error("Couldn't send message.", e);
            throw new IOException(e.getMessage());
        }
    }

    public long getContentLength()
    {
        // not known so we send negative value
        return -100;
    }

    public String getContentType()
    {
        return this.message.getEncoding();
    }

}
