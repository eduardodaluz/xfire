package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractSoapChannel;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.STAXUtils;

public class HttpSoapChannel
    extends AbstractSoapChannel
{
    public HttpSoapChannel(String uri, SoapHttpTransport transport)
    {
        setTransport(transport);
        setUri(uri);
    }

    public void open()
    {
    }

    public void send(MessageContext context, OutMessage message) throws XFireFault
    {
        if (message.getUri().equals(Channel.BACKCHANNEL_URI))
        {
            HttpServletResponse response = XFireServletController.getResponse();
            
            if (response == null)
            {
                throw new XFireRuntimeException("No backchannel exists for message");
            }
            
            Attachments atts = (Attachments) context.getProperty(Attachments.ATTACHMENTS_KEY);
            if (atts != null && atts.size() > 0)
            {
                // createMimeOutputStream(context);
            }

            try
            {
                OutputStream out = response.getOutputStream();
                XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());
                
                sendSoapMessage(message, writer, context);
                
                writer.flush();
                writer.close();
                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            sendViaClient(context, message);
        }
    }

    private void sendViaClient(MessageContext context, OutMessage message)
        throws XFireFault
    {
        HttpMessageSender sender = new HttpMessageSender();
        try
        {
            InMessage msg = sender.open();
            
            sender.send(message);
            
            getReceiver().onReceive(context, msg);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
