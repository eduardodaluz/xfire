package org.codehaus.xfire.loom;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * An instance of XFire that is managed by Loom / Phoenix.
 *
 * @author <a href="mailto:peter.royal@pobox.com">Peter Royal</a>
 */
public class LoomXFire extends DefaultXFire implements Serviceable
{
    private ServiceRegistry m_serviceRegistry;
    private TransportManager m_transportManager;

    public LoomXFire()
    {
        super( null, null ); // Use this to prevent component instantiation in the superclass default cxtor
    }

    public ServiceRegistry getServiceRegistry()
    {
        return m_serviceRegistry;
    }

    public TransportManager getTransportManager()
    {
        return m_transportManager;
    }

    public void service( final ServiceManager manager ) throws ServiceException
    {
        m_serviceRegistry = (ServiceRegistry)manager.lookup( ServiceRegistry.ROLE );
        m_transportManager = (TransportManager)manager.lookup( TransportManager.ROLE );
    }
}
