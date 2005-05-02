package org.codehaus.xfire.plexus;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceEndpoint;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ServiceRegistry
        extends DefaultServiceRegistry
        implements Initializable, Serviceable
{
    private ServiceLocator locator;

    public ServiceRegistry()
    {
        super();
    }

    public void initialize()
            throws Exception
    {
        Map services = locator.lookupMap(Service.ROLE);
        for (Iterator itr = services.values().iterator(); itr.hasNext();)
        {
            ServiceEndpoint service = (ServiceEndpoint) itr.next();
            register(service);
        }
    }

    /**
     * @param arg0
     */
    public void service(ServiceLocator locator)
    {
        this.locator = locator;
    }
}
