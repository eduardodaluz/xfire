package org.codehaus.xfire.service;

import java.util.Collection;

/**
 * The central place to register, unregister, and get information about 
 * services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface ServiceRegistry
{
	final public static String ROLE = ServiceRegistry.class.getName();
    
	Service getService( String serviceName );

    void register( Service service );
    
    void unregister( String serviceName );

	boolean hasService(String service);

	Collection getServices();
    
    //void enable( String name );
    
    //void disable( String name );
}
