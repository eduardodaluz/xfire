package org.codehaus.xfire.picocontainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.object.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.type.TypeMappingRegistry;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

/**
 * A Picocontainer's ComponentComposer which register default XFire components.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public final class DefaultXFireComponentsContainerComposer implements ContainerComposer {

    private final Log log = LogFactory.getLog(DefaultXFireComponentsContainerComposer.class);

    public void composeContainer(MutablePicoContainer pico, Object scope) {
        try {
            XFire xfire = XFireFactory.newInstance().getXFire();
            pico.registerComponentInstance(XFire.class, xfire);
            pico.registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(TransportManager.class, DefaultTransportManagerDelegator.class)));
            pico.registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(ServiceRegistry.class, DefaultServiceRegistryDelegator.class)));
            pico.registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(ServiceFactory.class, ObjectServiceFactory.class)));
            pico.registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(XFireServiceRegisterVisitor.class, XFireServiceRegisterVisitor.class)));
            pico.registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(TypeMappingRegistry.class, DefaultTypeMappingRegistry.class,
                    new Parameter[] { new ConstantParameter(new Boolean(true)) })));

            this.log.info("XFire default components configured.");
        } catch (Exception e) {
            this.log.error("An error occurred during the defaults XFire components registration.", e);
            throw new PicoRegistrationException("It was not possible to register defaults XFire components.", e);
        }

    }

}
