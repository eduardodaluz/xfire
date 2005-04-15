package org.codehaus.xfire.picocontainer.util;

import java.util.Collection;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * Abstract base class for ServiceRegistry delegators. Delegates all calls to
 * ServiceRegistry obtained by implementing class. All methods are just
 * delegations.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public abstract class AbstractServiceRegistryDelegator
    implements ServiceRegistry
{

    public abstract ServiceRegistry getServiceRegistry();

    public Service getService(String serviceName)
    {
        return this.getServiceRegistry().getService(serviceName);
    }

    public void register(Service service)
    {
        this.getServiceRegistry().register(service);
    }

    public void unregister(String serviceName)
    {
        this.getServiceRegistry().unregister(serviceName);
    }

    public boolean hasService(String service)
    {
        return this.getServiceRegistry().hasService(service);
    }

    public Collection getServices()
    {
        return this.getServiceRegistry().getServices();
    }

    public void addRegistrationEventListener(RegistrationEventListener listener)
    {
        this.addRegistrationEventListener(listener);
    }

    public void removeRegistrationEventListener(RegistrationEventListener listener)
    {
        this.getServiceRegistry().removeRegistrationEventListener(listener);
    }

}
