package org.codehaus.xfire.picocontainer;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceRegistry;

/**
 * Default ServiceRegistryDelegator's implementation which just use ServiceRegistry instance
 * obtained from XFire instance.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public final class DefaultServiceRegistryDelegator extends AbstractServiceRegistryDelegator {

    private final ServiceRegistry serviceRegistry;

    public DefaultServiceRegistryDelegator(final XFire xfire) {
        this.serviceRegistry = xfire.getServiceRegistry();
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

}
