package org.codehaus.xfire.picocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

public class ObjectServiceComponentAdapterFactory implements ComponentAdapterFactory {

    private ComponentAdapterFactory caf;

    public ObjectServiceComponentAdapterFactory(ComponentAdapterFactory caf) {
        this.caf = caf;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return new ObjectServiceComponentAdapter(caf.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

}
