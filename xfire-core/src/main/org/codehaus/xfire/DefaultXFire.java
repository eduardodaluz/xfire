package org.codehaus.xfire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.local.LocalTransport;
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
        final Service endpoint = findService(serviceName);
        
        if (endpoint == null)
        {
            throw new XFireRuntimeException("No such service: " + serviceName);
        }
        
        try
        {
            final Transport transport = getTransportManager().getTransport(LocalTransport.NAME);
            Channel channel = transport.createChannel(endpoint);
            
            // Verify that the transport can be used for this service.
            if (transport != null
                    &&
                    !getTransportManager().isEnabled(context.getServiceName(), transport.getName()))
            {
                throw new XFireFault("Service " + serviceName +
                                     " is unavailable for current transport.",
                                     XFireFault.SENDER);
            }

            context.setService(endpoint);
            
            InMessage inMessage = new InMessage(reader, channel.getUri());
            channel.receive(context, inMessage);
        }
        catch (Exception e)
        {
            logger.error("Could not initiate service operation.", e);
        }
    }

    protected Service findService(final String serviceName)
    {
        Service service = getServiceRegistry().getService(serviceName);
        
        if (service == null)
        {
            throw new XFireRuntimeException("Couldn't find service " + serviceName);
        }
        
        return service;
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
        catch (IOException e)
        {
            throw new XFireRuntimeException("Couldn't generate WSDL.", e);
        }
    }

    private WSDLWriter getWSDL(final String serviceName)
    {
        final Service service = findService(serviceName);
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
