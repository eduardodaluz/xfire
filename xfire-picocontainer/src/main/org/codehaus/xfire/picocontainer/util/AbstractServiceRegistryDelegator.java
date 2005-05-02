package org.codehaus.xfire.picocontainer.util;

import java.util.Collection;

import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * Abstract base class for ServiceRegistry delegators. Delegates all calls to ServiceRegistry obtained by implementing
 * class. All methods are just delegations.
 *
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public abstract class AbstractServiceRegistryDelegator
        implements ServiceRegistry
{

    public abstract ServiceRegistry getServiceRegistry();

    public ServiceEndpoint getServiceEndpoint(String serviceName)
    {
        return this.getServiceRegistry().getServiceEndpoint(serviceName);
    }

    public void register(ServiceEndpoint service)
    {
        this.getServiceRegistry().register(service);
    }

    public void unregister(String serviceName)
    {
        this.getServiceRegistry().unregister(serviceName);
    }

    public boolean hasServiceEndpoint(String service)
    {
        return this.getServiceRegistry().hasServiceEndpoint(service);
    }

    public Collection getServiceEndpoints()
    {
        return this.getServiceRegistry().getServiceEndpoints();
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
