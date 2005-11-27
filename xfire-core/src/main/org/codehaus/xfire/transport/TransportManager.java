package org.codehaus.xfire.transport;

import java.util.Collection;

import org.codehaus.xfire.service.Service;


/**
 * Registers transports for the SOAP services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface TransportManager
{
    String ROLE = TransportManager.class.getName();

    void register(Transport transport);

    void unregister(Transport transport);

    /**
     * Get a transport for a particular binding id.
     * @param id
     * @return
     */
    Transport getTransport(String id);

    /**
     * Find the best transport for a particular URI.
     * @param uri
     * @return
     */
    Transport getTransportForUri(String uri);
    
    Collection getTransportsForUri(String uri);
    
    /**
     * Get the transports applicable to a particular service.
     *
     * @param service
     * @return
     */
    Collection getTransports(Service service);

    Collection getTransports();

    void enableAll(Service service);

    void disableAll(Service service);

    void enable(Transport transport, Service service);

    void disable(Transport transport, Service service);

    /**
     * Determine if a transport is enabled for a particular service.
     *
     * @param serviceName   The name of the service.
     * @param transportName The name of the transport.
     * @return
     */
    boolean isEnabled(Transport transport, Service service);
}
