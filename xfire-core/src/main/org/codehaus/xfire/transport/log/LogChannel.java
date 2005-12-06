package org.codehaus.xfire.transport.log;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.STAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Channel that just logs message to stdout
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class LogChannel
    extends AbstractChannel
{
    public LogChannel( String uri, LogTransport transport )
    {
        setUri( uri );
        setTransport( transport );
    }

    public void open()
    {
    }

    public void send( final MessageContext context, final OutMessage message ) throws XFireException
    {
        OutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter( out, message.getEncoding() );

        message.getSerializer().writeMessage( message, writer, context );


        try
        {
            writer.close();
            out.close();
        }
        catch( IOException e )
        {
            throw new XFireException( "Unable to close stream", e );
        }
        catch( XMLStreamException e )
        {
            throw new XFireException( "Unable to close stream", e );
        }

        System.out.println( out.toString() );
    }

    public void close()
    {
    }
    
    public boolean isAsync()
    {
        return true;
    }
}
