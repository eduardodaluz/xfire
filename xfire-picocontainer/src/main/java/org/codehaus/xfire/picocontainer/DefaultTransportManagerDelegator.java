package org.codehaus.xfire.picocontainer;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.picocontainer.util.AbstractTransportManagerDelegator;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Default TransportManagerDelegator 's implementation which just use TransportManager instance
 * obtained from XFire instance.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public final class DefaultTransportManagerDelegator extends AbstractTransportManagerDelegator {

    private final TransportManager transportManager;

    public DefaultTransportManagerDelegator(final XFire xfire) {
        transportManager = xfire.getTransportManager();
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }

}
