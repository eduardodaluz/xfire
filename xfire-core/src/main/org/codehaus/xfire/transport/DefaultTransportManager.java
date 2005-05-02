package org.codehaus.xfire.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.event.RegistrationEvent;
import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * The default <code>TransportService</code> implementation.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultTransportManager
        extends AbstractXFireComponent
        implements TransportManager, RegistrationEventListener
{
    private Map services = new HashMap();
    private Map transports = new HashMap();

    protected DefaultTransportManager()
    {
    }

    public DefaultTransportManager(ServiceRegistry registry)
    {
        initializeTransports(registry);
    }

    /**
     * @param registry
     */
    protected void initializeTransports(ServiceRegistry registry)
    {
        for (Iterator itr = registry.getServiceEndpoints().iterator(); itr.hasNext();)
        {
            ServiceEndpoint endpoint = (ServiceEndpoint) itr.next();
            enableAll(endpoint.getName());
        }
        registry.addRegistrationEventListener(this);
    }


    public void register(Transport transport)
    {
        transports.put(transport.getName(), transport);

        for (Iterator itr = services.values().iterator(); itr.hasNext();)
        {
            Map serviceTransports = (Map) itr.next();
            serviceTransports.put(transport.getName(), transport);
        }
    }

    public void unregister(Transport transport)
    {
        transports.remove(transport);

        for (Iterator itr = services.values().iterator(); itr.hasNext();)
        {
            Map serviceTransports = (Map) itr.next();
            if (serviceTransports != null)
            {
                serviceTransports.remove(transport);
            }
        }
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     */
    public Transport getTransport(String name)
    {
        return (Transport) transports.get(name);
    }

    public void enable(String transport, String serviceName)
    {
        Map serviceTransports = (Map) services.get(serviceName);
        if (serviceTransports == null)
        {
            serviceTransports = new HashMap();
            services.put(serviceName, serviceTransports);
        }

        serviceTransports.put(transport, getTransport(transport));
    }

    public void disable(String transport, String serviceName)
    {
        Map serviceTransports = (Map) services.get(serviceName);
        if (serviceTransports == null)
        {
            return;
        }

        serviceTransports.remove(transport);
    }

    /**
     * @param service
     * @return
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     */
    public Collection getTransports(String service)
    {
        Map transports = ((Map) services.get(service));

        if (transports != null)
            return transports.values();
        else
            return null;
    }

    /**
     * @param serviceName
     */
    public void enableAll(String serviceName)
    {
        Map serviceTransports = (Map) services.get(serviceName);
        if (serviceTransports == null)
        {
            serviceTransports = new HashMap();
            services.put(serviceName, serviceTransports);
        }

        for (Iterator itr = transports.values().iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();

            serviceTransports.put(t.getName(), t);
        }
    }

    /**
     * @param serviceName
     */
    public void disableAll(String serviceName)
    {
        Map serviceTransports = (Map) services.get(serviceName);
        if (serviceTransports == null)
        {
            return;
        }

        for (Iterator itr = transports.values().iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();

            serviceTransports.remove(t);
        }
    }

    /**
     * @param service
     * @param transportName
     * @return
     */
    public boolean isEnabled(String service, String transportName)
    {
        Map serviceTransports = (Map) services.get(service);
        if (serviceTransports == null)
        {
            return false;
        }

        if (serviceTransports.containsKey(transportName))
        {
            return true;
        }

        return false;
    }

    /**
     * Notifies this <code>RegistrationEventListener</code> that the <code>ServiceEndpointRegistry</code> has registered
     * an endpoint.
     *
     * @param event an event object describing the source of the event
     */
    public void endpointRegistered(RegistrationEvent event)
    {
        enableAll(event.getEndpoint().getName());
    }

    /**
     * Notifies this <code>RegistrationEventListener</code> that the <code>ServiceEndpointRegistry</code> has
     * deregistered an endpoint.
     *
     * @param event an event object describing the source of the event
     */
    public void endpointUnregistered(RegistrationEvent event)
    {
        disableAll(event.getEndpoint().getName());
    }
}
