package org.codehaus.xfire.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.xfire.service.event.RegistrationEvent;
import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class DefaultServiceRegistry
        implements ServiceRegistry
{
    // maps string names to service endpoints
    private Map services = new HashMap();
    private List eventListeners = new ArrayList();

    /**
     * Returns the <code>ServiceEndpoint</code> with the given qualified name, if found. Returns <code>null</code> if
     * not found.
     *
     * @param name the service name.
     * @return the service endpoint, or <code>null</code> if not found.
     */
    public ServiceEndpoint getServiceEndpoint(String name)
    {
        return (ServiceEndpoint) services.get(name);
    }

    /**
     * Registers a given <code>ServiceEndpoint</code> with this registry.
     *
     * @param endpoint the endpoint.
     */
    public void register(ServiceEndpoint endpoint)
    {
        services.put(endpoint.getName(), endpoint);

        for (Iterator iterator = eventListeners.iterator(); iterator.hasNext();)
        {
            RegistrationEventListener listener = (RegistrationEventListener) iterator.next();
            RegistrationEvent event = new RegistrationEvent(this, endpoint);
            listener.endpointRegistered(event);
        }
    }

    /**
     * Unregisters the service with the given qualified name, if found.
     *
     * @param name the service name.
     */
    public void unregister(String name)
    {
        ServiceEndpoint endpoint = getServiceEndpoint(name);

        for (Iterator iterator = eventListeners.iterator(); iterator.hasNext();)
        {
            RegistrationEventListener listener = (RegistrationEventListener) iterator.next();
            RegistrationEvent event = new RegistrationEvent(this, endpoint);
            listener.endpointUnregistered(event);
        }

        services.remove(name);
    }

    /**
     * Indicates whether this registry has a service with the given name.
     *
     * @param name the service name.
     * @return <code>true</code> if this registry has a service with the given name; <code>false</code> otherwise.
     */
    public boolean hasServiceEndpoint(String name)
    {
        return services.containsKey(name);
    }

    /**
     * Returns all <code>ServiceEndpoint</code> registered to this registry.
     *
     * @return all service endpoints.
     */
    public Collection getServiceEndpoints()
    {
        return Collections.unmodifiableCollection(services.values());
    }

    /**
     * Add a listener for registration events.
     *
     * @param listener the listener.
     */
    public void addRegistrationEventListener(RegistrationEventListener listener)
    {
        eventListeners.add(listener);
    }

    /**
     * Remove a listener for registration events.
     *
     * @param listener the listener.
     */
    public void removeRegistrationEventListener(RegistrationEventListener listener)
    {
        eventListeners.remove(listener);
    }
}
