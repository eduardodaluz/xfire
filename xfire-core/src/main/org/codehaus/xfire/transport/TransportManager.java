package org.codehaus.xfire.transport;

import java.util.Collection;


/**
 * Registers transports for the SOAP services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface TransportManager
{
    String ROLE = TransportManager.class.getName();

    void register( Transport transport );
    
    void unregister( Transport transport );
    
    Transport getTransport( String id );
    
    /**
     * Get the transports applicable to a particular service.
     * @param service
     * @return
     */
    Collection getTransports(String service);
    
    void enableAll(String service);
    
    void disableAll(String service);
    
    void enable( String transport, String service );

    void disable( String transport, String service );

    /**
     * Determine if a transport is enabled for a particular service.
     * 
     * @param serviceName The name of the service.
     * @param name The name of the transport.
     * @return
     */
    boolean isEnabled(String serviceName, String name);
}
