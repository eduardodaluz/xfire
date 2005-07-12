package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.STAXUtils;

public class HttpSoapChannel
    extends AbstractChannel
{
    public HttpSoapChannel(String uri, SoapHttpTransport transport)
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
            
            Attachments atts = message.getAttachments();
            if (atts != null && atts.size() > 0)
            {
                throw new UnsupportedOperationException("Sending attachments isn't supported at this time.");
            }

            try
            {
                OutputStream out = response.getOutputStream();
                XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());
                
                message.getSerializer().writeMessage(message, writer, context);
                
                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                if (e instanceof XFireException)
                    throw (XFireException) e;
                
                throw new XFireException("Couldn't send message.", e);
            }
        }
        else
        {
            sendViaClient(context, message);
        }
    }

    private void sendViaClient(MessageContext context, OutMessage message)
        throws XFireException
    {
        HttpMessageSender sender = new HttpMessageSender(message.getUri(), message.getEncoding());
        try
        {
            sender.setAction(message.getAction());
            sender.open();
            
            OutputStream out = sender.getOutputStream();
            XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());

            message.getSerializer().writeMessage(message, writer, context);
            
            out.flush();
            out.close();

            getReceiver().onReceive(context, sender.getInMessage());
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
}
