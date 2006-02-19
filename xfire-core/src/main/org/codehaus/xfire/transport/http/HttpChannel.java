package org.codehaus.xfire.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.ByteDataSource;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.STAXUtils;

public class HttpChannel
    extends AbstractChannel
{
    private static final Log log = LogFactory.getLog(HttpChannel.class);
    
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
        if (message.getUri().equals(Channel.BACKCHANNEL_URI))
        {
            HttpServletResponse response = XFireServletController.getResponse();
            
            if (response == null)
            {
                throw new XFireRuntimeException("No backchannel exists for message");
            }
            
            try
            {
                Attachments atts = message.getAttachments();
                if (atts != null && atts.size() > 0)
                {
                    HttpChannel.writeAttachmentBody(context, message);
                    response.setContentType(atts.getContentType());

                    atts.write(response.getOutputStream());
                }
                else
                {
                    response.setContentType(getSoapMimeType(message));

                    HttpChannel.writeWithoutAttachments(context, message, response.getOutputStream());
                }
            }
            catch (IOException e)
            {
                throw new XFireException("Couldn't send message.", e);
            }
        }
        else
        {
            sendViaClient(context, message);
        }
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

    public static void writeAttachmentBody(MessageContext context, OutMessage message) 
        throws XFireException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeWithoutAttachments(context, message, bos);
        
        Attachments atts = message.getAttachments();
        
        ByteDataSource ds = new ByteDataSource(bos.toByteArray());
        ds.setContentType(getSoapMimeType(message));
        DataHandler dh = new DataHandler(ds);        
        
        SimpleAttachment att = new SimpleAttachment("soap-message.xml", dh);
       
        atts.setSoapMessage(att);
    }

    public static String getMimeType(AbstractMessage msg)
    {
        if (msg.getAttachments() != null && msg.getAttachments().size() > 0)
        {
            return msg.getAttachments().getContentType();
        }
        else
        {
            return getSoapMimeType(msg);
        }
    }
    
    public static String getSoapMimeType(AbstractMessage msg)
    {
        SoapVersion soap = msg.getSoapVersion();
        if (soap instanceof Soap11)
        {
            return "text/xml; charset=" + msg.getEncoding();
        }
        else if (soap instanceof Soap12)
        {
             return "application/soap+xml; charset=" +  msg.getEncoding();
        }
        else
        {
            return "text/xml; charset=" + msg.getEncoding();
        }
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
                getEndpoint().onReceive(context, sender.getInMessage());
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
    }

    public boolean isAsync()
    {
        return false;
    }
}
