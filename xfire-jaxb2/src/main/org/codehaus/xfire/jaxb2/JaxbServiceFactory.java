package org.codehaus.xfire.jaxb2;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.transport.TransportManager;

public class JaxbServiceFactory
    extends AnnotationServiceFactory
{

    public JaxbServiceFactory()
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager());
    }

    public JaxbServiceFactory(TransportManager transportManager)
    {
        super(new Jsr181WebAnnotations(),
              transportManager, 
              new AegisBindingProvider(new JaxbTypeRegistry()));
    }
}
