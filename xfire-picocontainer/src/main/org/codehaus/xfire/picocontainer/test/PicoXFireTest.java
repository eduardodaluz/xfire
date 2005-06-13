package org.codehaus.xfire.picocontainer.test;

import org.codehaus.xfire.picocontainer.PicoObjectServiceFactory;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

public class PicoXFireTest
    extends AbstractXFireTest
{

    private PicoObjectServiceFactory factory;

    private ObjectReference picoReference = new SimpleReference();

    public void setUp()
        throws Exception
    {
        super.setUp();
        picoReference.set(null);
    }

    public ServiceFactory getServiceFactory()
    {
        if (factory == null)
        {
            PicoObjectServiceFactory ofactory = new PicoObjectServiceFactory(picoReference,
                    getXFire().getTransportManager(), new MessageBindingProvider());

            ofactory.setStyle(SoapConstants.STYLE_MESSAGE);

            factory = ofactory;
        }

        return factory;
    }
    
    

    public void setServiceFactory(ServiceFactory value)
    {
        if (value instanceof PicoObjectServiceFactory) {
            factory = (PicoObjectServiceFactory) value;
            super.setServiceFactory(value);
            return;
        }
        
        fail("Service factory on this kind of test must be an PicoObjectServiceFactory.");
    }

    public MutablePicoContainer getPico()
    {
        if (picoReference.get() == null)
        {
            MutablePicoContainer pico = new DefaultPicoContainer();
            picoReference.set(pico);
            return pico;
        }

        return (MutablePicoContainer) picoReference.get();
    }
    
    public void setPico(MutablePicoContainer value) {
        picoReference.set(value);
    }

}
