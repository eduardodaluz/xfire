package org.codehaus.xfire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.wsdl.WSDLException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class DefaultXFire
    extends AbstractXFireComponent
    implements XFire
{
    private ServiceRegistry registry;

    private TransportManager transportManager;

    private static final Log logger = LogFactory.getLog( DefaultXFire.class );

    public DefaultXFire()
    {
        registry = new DefaultServiceRegistry();
        transportManager = new DefaultTransportManager();
    }

    public DefaultXFire( final ServiceRegistry registry,
                         final TransportManager transportManager )
    {
        this.registry = registry;
        this.transportManager = transportManager;
    }

    public void invoke( final XMLStreamReader reader,
                        final MessageContext context )
    {
        Handler handler = null;
        try
        {
            final Service service = findService( context.getServiceName() );
            context.setService( service );
            context.setXMLStreamReader( reader );

            if( service == null )
            {
                throw new XFireRuntimeException( "No such service." );
            }

            handler = service.getServiceHandler();

            handler.invoke( context );
        }
        catch( Exception e )
        {
            if( e instanceof XFireRuntimeException )
            {
                throw (XFireRuntimeException)e;
            }
            else if( handler != null )
            {
                logger.error( "Fault occurred.", e );
                handler.handleFault( e, context );
            }
            else
            {
                throw new XFireRuntimeException( "Couldn't process message.", e );
            }
        }
    }

    protected Service findService( final String serviceName )
    {
        return getServiceRegistry().getService( serviceName );
    }

    public void invoke( final InputStream stream,
                        final MessageContext context )
    {
        final XMLInputFactory factory = XMLInputFactory.newInstance();

        try
        {
            invoke( factory.createXMLStreamReader( stream ),
                    context );
        }
        catch( XMLStreamException e )
        {
            throw new XFireRuntimeException( "Couldn't parse stream.", e );
        }
    }

    public void generateWSDL( final String serviceName, final OutputStream out )
    {
        try
        {
            final WSDLWriter wsdl = getWSDL( serviceName );

            wsdl.write( out );
        }
        catch( WSDLException e )
        {
            throw new XFireRuntimeException( "Couldn't generate WSDL.", e );
        }
        catch( IOException e )
        {
            throw new XFireRuntimeException( "Couldn't generate WSDL.", e );
        }
    }

    private WSDLWriter getWSDL( final String serviceName )
        throws WSDLException
    {
        final Service service = findService( serviceName );

        return service.getWSDLWriter();
    }

    public ServiceRegistry getServiceRegistry()
    {
        return registry;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }
}
