package org.codehaus.xfire.picocontainer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.picocontainer.util.PicoObjectInvoker;
import org.codehaus.xfire.picocontainer.util.ThreadLocalObjectReference;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.object.ObjectService;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;
import org.picocontainer.defaults.ObjectReference;

/**
 * Pico's visitor that registers XFire services. If it's an ObjectService instance, it will change
 * Invoker to PicoObjectInvoker which will use an pico container to obtain the object instance.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public class XFireServiceRegisterVisitor extends AbstractPicoVisitor {

    protected final Log log = LogFactory.getLog(XFireServiceRegisterVisitor.class);

    private final ObjectReference picoReference;
    private final ServiceRegistry serviceRegistry;

    public XFireServiceRegisterVisitor(ServiceRegistry serviceRegistry) {
        this.picoReference = new ThreadLocalObjectReference();
        this.serviceRegistry = serviceRegistry;
    }

    public XFireServiceRegisterVisitor(ObjectReference picoReference, ServiceRegistry serviceRegistry) {
        this.picoReference = picoReference;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Set the picocontainer instance which will be used for the registred services to obtain
     * instances.
     */
    public void setPicocontainer(PicoContainer pico) {
        this.picoReference.set(pico);
    }

    public void visitContainer(final PicoContainer pico) {
        // The the actual container reference.
        this.setPicocontainer(pico);

        // Iterate over the container looking for ObjectServices and register them.
        Iterator i = pico.getComponentInstancesOfType(ObjectService.class).iterator();
        while (i.hasNext()) {
            ObjectService objectService = (ObjectService) i.next();
            objectService.setInvoker(new PicoObjectInvoker(this.picoReference, objectService.getServiceClass()));
            this.serviceRegistry.register(objectService);

            if (this.log.isInfoEnabled()) {
                this.log.info("Service \"" + objectService.getName() + "\" registred.");
            }
        }
    }

    public void visitComponentAdapter(ComponentAdapter ca) {
    // Do nothing
    }

    public void visitParameter(Parameter p) {
    // Do nothing
    }

}
