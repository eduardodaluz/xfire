package org.codehaus.xfire.loom.transport;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class LoomTransportManager extends DefaultTransportManager implements Initializable, Serviceable
{
    private ServiceRegistry m_registry;

    public void service( final ServiceManager manager ) throws ServiceException
    {
        m_registry = (ServiceRegistry)manager.lookup(ServiceRegistry.class.getName());
    }

    public void initialize() throws Exception
    {
        initializeTransports(m_registry);
    }
}
