package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Hashtable;
import org.codehaus.xfire.AbstractXFireComponent;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class DefaultServiceRegistry
    extends AbstractXFireComponent
    implements ServiceRegistry
{
    private Hashtable services;
    
    public DefaultServiceRegistry()
    {
        services = new Hashtable();
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
	}
    
	/**
	 * @see org.codehaus.xfire.service.ServiceRegistry#unregister(java.lang.String)
	 */
	public void unregister( String serviceName )
	{
		services.remove( getService( serviceName ) );
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
}
