package org.codehaus.xfire.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class DefaultServiceRegistry
    extends AbstractXFireComponent
    implements ServiceRegistry
{
    private Hashtable services;
    
    private List eventListeners;
    
    public DefaultServiceRegistry()
    {
        services = new Hashtable();
        eventListeners = new ArrayList();
    }
    
	/**
	 * @see org.codehaus.xfire.service.ServiceRegistry#getServiceDescriptor(java.lang.String)
	 */
	public Service getService( String serviceName )
	{
		return (Service) services.get( serviceName );
	}

    /**
	 * @see org.codehaus.xfire.service.ServiceRegistry#register(org.codehaus.xfire.service.ServiceDescriptor)
	 */
	public void register( Service service )
	{
        services.put( service.getName(), service );
        
        for (Iterator itr = eventListeners.iterator(); itr.hasNext();)
        {
            RegistrationEventListener listener = (RegistrationEventListener) itr.next();
            listener.onRegister(service);
        }
	}
    
	/**
	 * @see org.codehaus.xfire.service.ServiceRegistry#unregister(java.lang.String)
	 */
	public void unregister( String serviceName )
	{
        Service service = getService( serviceName );
		
        for (Iterator itr = eventListeners.iterator(); itr.hasNext();)
        {
            RegistrationEventListener listener = (RegistrationEventListener) itr.next();
            listener.onUnregister(service);
        }

        services.remove( service );
	}

	/**
	 * @see org.codehaus.xfire.service.ServiceRegistry#hasService(java.lang.String)
	 */
	public boolean hasService(String service)
	{
		return services.containsKey( service );
	}

	/**
	 * @see org.codehaus.xfire.service.ServiceRegistry#getServices()
	 */
	public Collection getServices()
	{
		return services.values();
	}

    /**
     * @param listener
     */
    public void addRegistrationEventListener(RegistrationEventListener listener)
    {
        eventListeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeRegistrationEventListener(RegistrationEventListener listener)
    {
        eventListeners.remove(listener);
    }
}
