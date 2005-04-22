package org.codehaus.xfire.service.assembler;

import org.codehaus.xfire.service.ServiceInfo;

/**
 * Interface to be implemented by all classes that can create {@link org.codehaus.xfire.service.ServiceInfo} objects.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public interface ServiceInfoAssembler
{
    /**
     * Creates the <code>ServiceInfo</code>.
     *
     * @return the service description.
     */
    ServiceInfo getServiceInfo();
}
