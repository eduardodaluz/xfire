package org.codehaus.xfire.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
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
    Map services;
    Map transports;
    
    protected DefaultTransportManager()
    {
        services = new HashMap();
        transports = new HashMap();
    }

    public DefaultTransportManager(ServiceRegistry registry)
    {
        services = new HashMap();
        transports = new HashMap();
        
        initializeTransports(registry);
    }

    /**
     * @param registry
     */
    protected void initializeTransports(ServiceRegistry registry)
    {
        for (Iterator itr = registry.getServices().iterator(); itr.hasNext();)
        {
            Service service = (Service) itr.next();
            enableAll(service.getName());
        }
        registry.addRegistrationEventListener(this);
    }
    
    /**
     * @see org.codehaus.xfire.transport.TransportManager#register(org.codehaus.xfire.transport.Transport)
     */
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
            if (serviceTransports != null )
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

    /**
     * @see org.codehaus.xfire.transport.TransportManager#enable(java.lang.String, java.lang.String)
     */
    public void enable(String transport, String service)
    {
        Map serviceTransports = (Map) services.get(service);
        if ( serviceTransports == null )
        {
            serviceTransports = new HashMap();
            services.put(service, serviceTransports);
        }
        
        serviceTransports.put(transport, getTransport(transport));
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#disable(java.lang.String, java.lang.String)
     */
    public void disable(String transport, String service)
    {
        Map serviceTransports = (Map) services.get(service);
        if ( serviceTransports == null )
        {
           return;
        }
        
        serviceTransports.remove(transport);
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     * @param service
     * @return
     */
    public Collection getTransports(String service)
    {
        return ((Map) services.get(service)).values();
    }

    /**
     * @param service
     */
    public void enableAll(String service)
    {
        Map serviceTransports = (Map) services.get(service);
        if (serviceTransports == null)
        {
            serviceTransports = new HashMap();
            services.put(service, serviceTransports);
        }
        
        for (Iterator itr = transports.values().iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();

            serviceTransports.put(t.getName(), t);
        }
    }

    /**
     * @param service
     */
    public void disableAll(String service)
    {
        Map serviceTransports = (Map) services.get(service);
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
     */
    public void onRegister(Service service)
    {
        enableAll(service.getName());
    }

    /**
     * @param service
     */
    public void onUnregister(Service service)
    {
        disableAll(service.getName());
    }

    /**
     * @param service
     * @param name
     * @return
     */
    public boolean isEnabled(String service, String name)
    {
        Map serviceTransports = (Map) services.get(service);
        if (serviceTransports == null)
        {
            return false;
        }
        
        if (serviceTransports.containsKey(name))
        {
            return true;
        }
        
        return false;
    }
}
