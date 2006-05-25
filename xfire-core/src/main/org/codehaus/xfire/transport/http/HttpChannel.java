package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.STAXUtils;

public class HttpChannel
    extends AbstractChannel
{
    private static final Log log = LogFactory.getLog(HttpChannel.class);
    private Map properties = new HashMap();
    
    public HttpChannel(String uri, HttpTransport transport)
    {
        setTransport(transport);
        setUri(uri);
    }

    public void open()
    {
    }

    public void send(MessageContext context, OutMessage message) throws XFireException
    {
        sendViaClient(context, message);
    }

    public static void writeWithoutAttachments(MessageContext context, OutMessage message, OutputStream out) 
        throws XFireException
    {
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding(), context);
        
        message.getSerializer().writeMessage(message, writer, context);
        
        try
        {
            writer.flush();
        }
        catch (XMLStreamException e)
        {
            log.error(e);
            throw new XFireException("Couldn't send message.", e);
        }
    }

    public static String getSoapMimeType(AbstractMessage msg)
    {
        String ct;
        SoapVersion soap = msg.getSoapVersion();
        if (soap instanceof Soap11)
        {
            ct = "text/xml; charset=" + msg.getEncoding();
        }
        else if (soap instanceof Soap12)
        {
             return "application/soap+xml; charset=" + msg.getEncoding();
        }
        else
        {
            return "text/xml; charset=" + msg.getEncoding();
        }
        
        return ct;
    }

    protected void sendViaClient(MessageContext context, OutMessage message)
        throws XFireException
    {
        AbstractMessageSender sender;
        
        try
        {
            Class chms = ClassLoaderUtils.loadClass("org.codehaus.xfire.transport.http.CommonsHttpMessageSender", getClass());
            Constructor constructor = chms.getConstructor(new Class[] {OutMessage.class, MessageContext.class});
            sender = (AbstractMessageSender) constructor.newInstance(new Object[] { message, context });
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.debug("Could not load commons http client. Using buggy SimpleMessageSender instead.");
                
            sender = new SimpleMessageSender(message, context);
        }
        
        try
        {
            sender.open();
            
            sender.send();

            if (sender.hasResponse())
            {   
                InMessage inMessage = sender.getInMessage();
                inMessage.setChannel(this);
                getEndpoint().onReceive(context, inMessage);
            }
        }
        catch (IOException e)
        {
            throw new XFireException("Couldn't send message.", e);
        }
        finally
        {
            sender.close();
        }
    }

    public void close()
    {
        properties.clear();
        
        super.close();
    }

    public boolean isAsync()
    {
        return false;
    }
    
    public Object getProperty(String key)
    {
        return properties.get(key);
    }
    
    public void setProperty(String key, Object value)
    {
        properties.put(key, value);
    }
}
