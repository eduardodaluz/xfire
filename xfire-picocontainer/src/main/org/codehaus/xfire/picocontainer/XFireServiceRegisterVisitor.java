package org.codehaus.xfire.picocontainer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.picocontainer.util.PicoObjectInvoker;
import org.codehaus.xfire.picocontainer.util.ThreadLocalObjectReference;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.ObjectService;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;
import org.picocontainer.defaults.ObjectReference;

/**
 * Pico's visitor that registers XFire services. If it's an ObjectService
 * instance it will change Invoker to PicoObjectInvoker which will use an pico
 * container to obtain the object instance.
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
     * Set the picocontainer instance which will be used for the registred
     * services to obtain instances.
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
        Iterator i = pico.getComponentInstancesOfType(Service.class).iterator();
        while (i.hasNext())
        {
            Service service = (ObjectService) i.next();
            if (service instanceof ObjectService)
            {
                prepareObjectService((ObjectService) service);
            }
            serviceRegistry.register(service);

            if (log.isInfoEnabled())
            {
                log.info("Service \"" + service.getName() + "\" registred.");
            }
        }
    }

    public void visitComponentAdapter(ComponentAdapter ca)
    {
        if (ca instanceof ObjectServiceComponentAdapter)
        {
            ObjectService objectService;
            if (ca.getComponentKey() instanceof Class)
            {
                objectService = (ObjectService) serviceFactory.create((Class) ca.getComponentKey());
            }
            else
            {
                objectService = (ObjectService) serviceFactory.create(ca
                        .getComponentImplementation());
            }

            prepareObjectService(objectService);
            serviceRegistry.register(objectService);

            if (log.isInfoEnabled())
            {
                log.info("Service \"" + objectService.getName() + "\" registred.");
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
    protected void prepareObjectService(ObjectService objectService)
    {
        objectService.setInvoker(new PicoObjectInvoker(picoReference, objectService
                .getServiceClass()));
    }
}
