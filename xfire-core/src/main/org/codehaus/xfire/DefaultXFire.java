package org.codehaus.xfire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.wsdl.WSDLException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
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
    
    private static Logger logger = Logger.getLogger(DefaultXFire.class);
    
    public DefaultXFire()
    {
        registry = new DefaultServiceRegistry();
        transportManager = new DefaultTransportManager();
    }
    
    public DefaultXFire(ServiceRegistry registry, 
                        TransportManager transportManager)
    {
        this.registry = registry;
        this.transportManager = transportManager;
    }
    
    /**
     * @see org.codehaus.xfire.XFire#invoke(java.lang.String, javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter, org.codehaus.xfire.transport.Invocation)
     */
    public void invoke( XMLStreamReader reader, 
                        MessageContext context )
    {
        Handler handler = null;
        try
        {
            Service service = findService( context.getServiceName() );
            context.setService(service);
            
            if ( service == null )
            {
                throw new XFireRuntimeException("No such service.");
            }
            
            handler = service.getServiceHandler();
            
            handler.invoke( context, reader );
        }
        catch (Exception e)
        {
            if ( e instanceof XFireRuntimeException )
            {
                throw (XFireRuntimeException) e;
            }
            else if ( handler != null )
            {
                logger.error("Fault occurred.", e);
                handler.handleFault( e, context );
            }
            else
            {
                throw new XFireRuntimeException("Couldn't process message.", e);
            }
        }
    }

    /**
     * @param context
     * @return
     * @throws ComponentLookupException
     */
    protected Service findService( String serviceName ) 
    {
        return getServiceRegistry().getService( serviceName );
    }

    /**
     * @see org.codehaus.xfire.XFire#invoke(java.lang.String, java.io.InputStream, javax.xml.stream.XMLStreamWriter, org.codehaus.xfire.transport.Invocation)
     */
    public void invoke( InputStream stream, 
                        MessageContext context )
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        
        try
        {
            invoke( factory.createXMLStreamReader(stream),
                    context );
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }    

    /**
     * @see org.codehaus.xfire.XFire#generateWSDL(java.lang.String)
     */
    public void generateWSDL(String serviceName, OutputStream out)
    {
        try
        {
            WSDLWriter wsdl = getWSDL(serviceName);
            
            wsdl.write( out );
        } 
        catch (WSDLException e)
        {
            throw new XFireRuntimeException( "Couldn't generate WSDL.", e );
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException( "Couldn't generate WSDL.", e );
        }
    }
    
    /**
	 * @param serviceName
	 * @return
	 * @throws ServiceException
	 * @throws WSDLException
	 */
	private WSDLWriter getWSDL(String serviceName) 
        throws WSDLException
	{
		Service service = findService( serviceName );
		
		WSDLWriter wsdl = service.getWSDL();
		return wsdl;
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
