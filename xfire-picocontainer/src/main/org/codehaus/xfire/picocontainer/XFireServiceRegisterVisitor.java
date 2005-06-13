package org.codehaus.xfire.picocontainer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;

/**
 * Looks for Services then register them on XFire.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public class XFireServiceRegisterVisitor
    extends AbstractPicoVisitor
{

    protected final Log log = LogFactory.getLog(XFireServiceRegisterVisitor.class);

    private final ServiceRegistry serviceRegistry;

    public XFireServiceRegisterVisitor(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void visitContainer(final PicoContainer pico)
    {
        // Iterate over the container looking for services and register them.
        Iterator i = pico.getComponentInstancesOfType(Service.class).iterator();
        while (i.hasNext())
        {
            Service endpoint = (Service) i.next();
            serviceRegistry.register(endpoint);

            if (log.isInfoEnabled())
            {
                log.info("Service \"" + endpoint.getServiceInfo().getName() + "\" registred.");
            }
        }
    }

    public void visitComponentAdapter(ComponentAdapter ca)
    {
        // Do nothing
    }

    public void visitParameter(Parameter p)
    {
        // Do nothing
    }

}
