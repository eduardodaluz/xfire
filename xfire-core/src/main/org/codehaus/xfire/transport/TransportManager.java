package org.codehaus.xfire.transport;

import java.util.Set;

/**
 * Registers transports for the SOAP services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface TransportManager
{
    String ROLE = TransportManager.class.getName();

    void register( Transport transport );
    
    Set getTransports( String service );
    
    void enable( String transport, String service );

    void disable( String transport, String service );
}
