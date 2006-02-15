package org.codehaus.xfire.transport.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.util.STAXUtils;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class LocalChannel
    extends AbstractChannel
{
    private static final Log logger = LogFactory.getLog( LocalChannel.class );

    protected static final String SENDER_URI = "senderUri";
    protected static final String OLD_CONTEXT = "urn:xfire:transport:local:oldContext";

    private final Session session;

    public LocalChannel( String uri, LocalTransport transport, Session session )
    {
        this.session = session;
        setUri( uri );
        setTransport( transport );
    }

    public void open()
    {
    }

    public void send( final MessageContext context, final OutMessage message ) throws XFireException
    {
        if( message.getUri().equals( Channel.BACKCHANNEL_URI ) )
        {
            final OutputStream out = (OutputStream)context.getProperty( Channel.BACKCHANNEL_URI );
            if( out != null )
            {
                final XMLStreamWriter writer = STAXUtils.createXMLStreamWriter( out, message.getEncoding(),context );

                message.getSerializer().writeMessage( message, writer, context );
            }
            else
            {
                MessageContext oldContext = (MessageContext)context.getProperty( OLD_CONTEXT );

                sendViaNewChannel( context, oldContext, message, (String)context.getProperty( SENDER_URI ) );
            }
        }
        else
        {
            MessageContext receivingContext = new MessageContext();
            receivingContext.setXFire( context.getXFire() );
            receivingContext.setService( getService( context.getXFire(), message.getUri() ) );
            receivingContext.setProperty( OLD_CONTEXT, context );
            receivingContext.setProperty( SENDER_URI, getUri() );
            receivingContext.setSession( session );

            sendViaNewChannel( context, receivingContext, message, message.getUri() );
        }
    }

    protected Service getService( XFire xfire, String uri ) throws XFireException
    {
        if( null == xfire )
        {
            logger.warn( "No XFire instance in context, unable to determine service" );
            return null;
        }

        int i = uri.indexOf( "//" );

        if( i == -1 )
        {
            throw new XFireException( "Malformed service URI" );
        }

        String name = uri.substring( i + 2 );
        Service service = xfire.getServiceRegistry().getService( name );

        if( null == service )
        {
            //TODO this should be an exception...
            logger.warn( "Unable to locate '" + name + "' in ServiceRegistry" );
        }

        return service;
    }

    private void sendViaNewChannel( final MessageContext context,
                                    final MessageContext receivingContext,
                                    final OutMessage message,
                                    final String uri ) throws XFireException
    {
        try
        {
            final PipedInputStream stream = new PipedInputStream();
            final PipedOutputStream outStream = new PipedOutputStream( stream );

            final Channel channel;
            try
            {
                channel = getTransport().createChannel( uri );
            }
            catch( Exception e )
            {
                throw new XFireException( "Couldn't create channel.", e );
            }

            Thread writeThread = new Thread( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        final XMLStreamWriter writer =
                            STAXUtils.createXMLStreamWriter( outStream, message.getEncoding(),context );
                        message.getSerializer().writeMessage( message, writer, context );

                        writer.close();
                        outStream.close();

                    }
                    catch( Exception e )
                    {
                        throw new XFireRuntimeException( "Couldn't write stream.", e );
                    }
                }

                ;
            } );

            Thread readThread = new Thread( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        final XMLStreamReader reader = STAXUtils.createXMLStreamReader((InputStream) stream, message.getEncoding(),context );
                        final InMessage inMessage = new InMessage( reader, uri );
                        inMessage.setEncoding( message.getEncoding() );

                        channel.receive( receivingContext, inMessage );

                        reader.close();
                        stream.close();
                    }
                    catch( Exception e )
                    {
                        throw new XFireRuntimeException( "Couldn't read stream.", e );
                    }
                }

                ;
            } );

            writeThread.start();
            readThread.start();

            try
            {
                writeThread.join();
            }
            catch( InterruptedException e )
            {
                //ignore is ok
            }
        }
        catch( IOException e )
        {
            throw new XFireRuntimeException( "Couldn't create stream.", e );
        }
    }

    public void close()
    {
    }
    
    public boolean isAsync()
    {
        return true;
    }
}
