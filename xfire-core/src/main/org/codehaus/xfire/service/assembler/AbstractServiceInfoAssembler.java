package org.codehaus.xfire.service.assembler;

import org.codehaus.xfire.service.ServiceInfo;

/**
 * Abstract implementation of the {@link ServiceInfoAssembler} interface. It creates the {@link ServiceInfo} object, but
 * uses abstract template methods to populate it.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public abstract class AbstractServiceInfoAssembler
        implements ServiceInfoAssembler
{

    /**
     * Creates an instance of the <code>ServiceInfo</code> class, and populates it through calls to the subclass.
     *
     * @return the populated service info.
     * @see #populateServiceInfo(org.codehaus.xfire.service.ServiceInfo)
     */
    public final ServiceInfo getServiceInfo()
    {
        ServiceInfo serviceInfo = new ServiceInfo();
        populateServiceInfo(serviceInfo);
        return serviceInfo;
    }

    /**
     * Abstract template method that gets called after the <code>ServiceInfo</code> instance has been constructed but
     * before it is returned.
     *
     * @param serviceInfo the constructed service info.
     */
    protected abstract void populateServiceInfo(ServiceInfo serviceInfo);

}
