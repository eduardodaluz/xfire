package org.codehaus.xfire.picocontainer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.picocontainer.util.PicoObjectInvoker;
import org.codehaus.xfire.picocontainer.util.ThreadLocalObjectReference;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;
import org.picocontainer.defaults.ObjectReference;

/**
 * Pico's visitor that registers XFire services. If it's an ObjectService instance it will change Invoker to
 * PicoObjectInvoker which will use an pico container to obtain the object instance.
 *
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public class XFireServiceRegisterVisitor
        extends AbstractPicoVisitor
{

    protected final Log log = LogFactory.getLog(XFireServiceRegisterVisitor.class);

    private final ObjectReference picoReference;

    private final ServiceRegistry serviceRegistry;

    private final ServiceFactory serviceFactory;

    public XFireServiceRegisterVisitor(ServiceRegistry serviceRegistry,
                                       ServiceFactory serviceFactory)
    {
        picoReference = new ThreadLocalObjectReference();
        this.serviceRegistry = serviceRegistry;
        this.serviceFactory = serviceFactory;
    }

    public XFireServiceRegisterVisitor(ObjectReference picoReference,
                                       ServiceRegistry serviceRegistry, ServiceFactory serviceFactory)
    {
        this.picoReference = picoReference;
        this.serviceRegistry = serviceRegistry;
        this.serviceFactory = serviceFactory;
    }

    /**
     * Set the picocontainer instance which will be used for the registred services to obtain instances.
     */
    public void setPicocontainer(PicoContainer pico)
    {
        picoReference.set(pico);
    }

    public void visitContainer(final PicoContainer pico)
    {
        // Set the actual container reference, necessary for PicoInvoker.
        setPicocontainer(pico);

        // Iterate over the container looking for services and register them.
        Iterator i = pico.getComponentInstancesOfType(ServiceEndpoint.class).iterator();
        while (i.hasNext())
        {
            ServiceEndpoint endpoint = (ServiceEndpoint) i.next();
            prepareObjectService(endpoint);
            serviceRegistry.register(endpoint);

            if (log.isInfoEnabled())
            {
                log.info("Service \"" + endpoint.getService().getName() + "\" registred.");
            }
        }
    }

    public void visitComponentAdapter(ComponentAdapter ca)
    {
        if (ca instanceof ObjectServiceComponentAdapter)
        {
            ServiceEndpoint endpoint;
            if (ca.getComponentKey() instanceof Class)
            {
                endpoint = serviceFactory.create((Class) ca.getComponentKey());
            }
            else
            {
                endpoint = serviceFactory.create(ca.getComponentImplementation());
            }

            prepareObjectService(endpoint);
            serviceRegistry.register(endpoint);

            if (log.isInfoEnabled())
            {
                log.info("Service \"" + endpoint.getService().getName() + "\" registered.");
            }
        }
    }

    public void visitParameter(Parameter p)
    {
        // Do nothing
    }

    /**
     * Prepare an ObjectService to use pico.
     */
    protected void prepareObjectService(ServiceEndpoint endpoint)
    {
        endpoint.setInvoker(new PicoObjectInvoker(picoReference,
                                                  endpoint.getService().getServiceClass()));
    }
}
