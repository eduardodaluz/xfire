package org.codehaus.xfire.plexus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectService;

/**
 * Invokes a Plexus service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 18, 2004
 */
public class ServiceInvoker
	extends ObjectInvoker
{
    private static Log logger = LogFactory.getLog(ServiceInvoker.class.getName());
    
    private ServiceLocator locator;

    public ServiceInvoker( ServiceLocator locator )
    {
        this.locator = locator;
    }

    public Object createServiceObject(ObjectService service)
        throws XFireFault
    {
        try
        {
            return locator.lookup(service.getServiceClass().getName());
        }
        catch (ComponentLookupException e)
        {
            throw new XFireFault("Couldn't find service object.", e, XFireFault.RECEIVER);
        }
    }

}
