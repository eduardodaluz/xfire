package org.codehaus.xfire.picocontainer;

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
        this.getTransportManager().register(transport);
    }

    public void unregister(Transport transport) {
        this.getTransportManager().unregister(transport);
    }

    public Transport getTransport(String id) {
        return this.getTransportManager().getTransport(id);
    }

    public Collection getTransports(String service) {
        return this.getTransportManager().getTransports(service);
    }

    public void enableAll(String service) {
        this.getTransportManager().enableAll(service);
    }

    public void disableAll(String service) {
        this.getTransportManager().disableAll(service);
    }

    public void enable(String transport, String service) {
        this.getTransportManager().enable(transport, service);
    }

    public void disable(String transport, String service) {
        this.getTransportManager().disable(transport, service);
    }

    public boolean isEnabled(String serviceName, String name) {
        return this.getTransportManager().isEnabled(serviceName, name);
    }

}
