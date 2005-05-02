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
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceEndpointAdapter;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.Transport;
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

    private static final Log logger = LogFactory.getLog(DefaultXFire.class);

    public DefaultXFire()
    {
        registry = new DefaultServiceRegistry();
        transportManager = new DefaultTransportManager(registry);
    }

    public DefaultXFire(final ServiceRegistry registry,
                        final TransportManager transportManager)
    {
        this.registry = registry;
        this.transportManager = transportManager;
    }

    public void invoke(final XMLStreamReader reader,
                       final MessageContext context)
    {
        final String serviceName = context.getServiceName();
        final ServiceEndpoint endpoint = findService(serviceName);

        Handler handler = null;
        try
        {
            final Transport transport = context.getTransport();
            
            // Verify that the transport can be used for this service.
            if (transport != null
                    &&
                    !getTransportManager().isEnabled(context.getServiceName(), transport.getName()))
            {
                throw new XFireFault("Service " + serviceName +
                                     " is unavailable for current transport.",
                                     XFireFault.SENDER);
            }

            context.setService(new ServiceEndpointAdapter(endpoint));
            context.setXMLStreamReader(reader);

            if (endpoint == null)
            {
                throw new XFireRuntimeException("No such service: " + serviceName);
            }

            handler = endpoint.getServiceHandler();

            handler.invoke(context);
        }
        catch (Exception e)
        {
            handleException(context, endpoint, handler, e);
        }
    }

    /**
     * @param context
     * @param endpoint
     * @param handler
     * @param e
     */
    protected void handleException(final MessageContext context,
                                   final ServiceEndpoint endpoint,
                                   final Handler handler,
                                   final Exception e)
    {
        if (e instanceof XFireRuntimeException)
        {
            throw (XFireRuntimeException) e;
        }
        else if (handler != null)
        {
            XFireFault fault = XFireFault.createFault(e);

            if (logger.isDebugEnabled())
            {
                logger.debug("Fault occurred.", fault);
            }

            handler.handleFault(fault, context);

            if (endpoint.getFaultHandler() != null)
                endpoint.getFaultHandler().handleFault(fault, context);
        }
        else
        {
            throw new XFireRuntimeException("Couldn't process message.", e);
        }
    }

    protected ServiceEndpoint findService(final String serviceName)
    {
        return getServiceEndpointRegistry().getServiceEndpoint(serviceName);
    }

    public void invoke(final InputStream stream,
                       final MessageContext context)
    {
        final XMLInputFactory factory = XMLInputFactory.newInstance();

        try
        {
            invoke(factory.createXMLStreamReader(stream),
                   context);
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }

    public void generateWSDL(final String serviceName, final OutputStream out)
    {
        try
        {
            final WSDLWriter wsdl = getWSDL(serviceName);

            wsdl.write(out);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Couldn't generate WSDL.", e);
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Couldn't generate WSDL.", e);
        }
    }

    private WSDLWriter getWSDL(final String serviceName)
            throws WSDLException
    {
        final ServiceEndpoint service = findService(serviceName);

        return service.getWSDLWriter();
    }

    public ServiceRegistry getServiceEndpointRegistry()
    {
        return registry;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }
}
