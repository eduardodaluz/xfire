package org.codehaus.xfire.picocontainer.util;

import java.util.Collection;

import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Abstract base class for TransportManager delegators. Delegates all calls to TransportManager
 * obtained by implementing class. All methods are just delegations.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public abstract class AbstractTransportManagerDelegator implements TransportManager {

    public abstract TransportManager getTransportManager();

    public void register(Transport transport) {
        getTransportManager().register(transport);
    }

    public void unregister(Transport transport) {
        getTransportManager().unregister(transport);
    }

    public Transport getTransport(String id) {
        return getTransportManager().getTransport(id);
    }

    public Collection getTransports(String service) {
        return getTransportManager().getTransports(service);
    }

    public void enableAll(String service) {
        getTransportManager().enableAll(service);
    }

    public void disableAll(String service) {
        getTransportManager().disableAll(service);
    }

    public void enable(String transport, String service) {
        getTransportManager().enable(transport, service);
    }

    public void disable(String transport, String service) {
        getTransportManager().disable(transport, service);
    }

    public boolean isEnabled(String serviceName, String name) {
        return getTransportManager().isEnabled(serviceName, name);
    }

}