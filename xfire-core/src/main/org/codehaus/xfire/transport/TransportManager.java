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
    
    Transport getTransport( String id );
    
    /**
     * Get the transports applicable to a particular service.
     * @param service
     * @return
     */
    Collection getTransports(String service);
    
    void enable( String transport, String service );

    void disable( String transport, String service );
}
