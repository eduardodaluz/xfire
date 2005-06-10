package org.codehaus.xfire.transport.local;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.AbstractSoapChannel;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.STAXUtils;

public class LocalChannel
    extends AbstractSoapChannel
{
    private String uri;
    protected static final String SENDER_URI = "senderUri";
    protected static final String OLD_CONTEXT = "urn:xfire:transport:local:oldContext";
    
    public LocalChannel(String uri, LocalTransport transport)
    {
        setUri(uri);
        setTransport(transport);
    }

    public void open()
    {
    }

    public void send(final MessageContext context, final OutMessage message) throws XFireException
    {
        if (message.getUri().equals(Channel.BACKCHANNEL_URI))
        {
            final OutputStream out = (OutputStream) context.getProperty(Channel.BACKCHANNEL_URI);
            
            if (out != null)
            {
                final XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());
                
                sendSoapMessage(message, writer, context);
            }
            else
            {
                MessageContext oldContext = (MessageContext) context.getProperty(OLD_CONTEXT);
                
                sendViaNewChannel(context, oldContext, message, (String) context.getProperty(SENDER_URI));
            }
        }
        else
        {
            MessageContext receivingContext = new MessageContext();
            receivingContext.setService(getService());
            receivingContext.setProperty(OLD_CONTEXT, context);
            receivingContext.setProperty(SENDER_URI, getUri());
            
            sendViaNewChannel(context, receivingContext, message, message.getUri());
        }
    }

    private void sendViaNewChannel(final MessageContext context,
                                   final MessageContext receivingContext,
                                   final OutMessage message,
                                   final String uri) throws XFireException
    {
        try
        {
            final PipedInputStream stream = new PipedInputStream();
            final PipedOutputStream outStream = new PipedOutputStream(stream);
            
            final Channel channel;
            try
            {
                channel = getTransport().createChannel(uri);
            }
            catch (Exception e)
            {
                throw new XFireException("Couldn't create channel.", e);
            }

            Thread readThread = new Thread(new Runnable() 
            {
                public void run() 
                {
                   try
                    {
                       final XMLStreamReader reader = STAXUtils.createXMLStreamReader(stream, message.getEncoding());
                       final InMessage inMessage = new InMessage(reader, uri);
                       inMessage.setEncoding(message.getEncoding());

                       channel.receive(receivingContext, inMessage);
                       
                       reader.close();
                       stream.close();
                    }
                    catch (Exception e)
                    {
                        throw new XFireRuntimeException("Couldn't read stream.", e);
                    }
                };
            });
            
            Thread writeThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        final XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(outStream, message.getEncoding());
                        sendSoapMessage(message, writer, context);
                        
                        writer.flush();
                        writer.close();
                        
                        outStream.flush();
                        outStream.close();
                    }
                    catch (Exception e)
                    {
                        throw new XFireRuntimeException("Couldn't write stream.", e);
                    }
                };
            });
            
            writeThread.start();
            readThread.start();
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Couldn't create stream.", e);
        }
    }

    public void receive(MessageContext context, InMessage message)
    {
        context.setService(getService());
        
        super.receive(context, message);
    }

    public void close()
    {
    }
}
