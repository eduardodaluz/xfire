package org.codehaus.xfire.jaxb2;

import javax.xml.bind.JAXBContext;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.transport.TransportManager;

public class JaxbServiceFactory
    extends AnnotationServiceFactory
{
    public JaxbServiceFactory()
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager(), null);
    }

    public JaxbServiceFactory(JAXBContext jaxbContext)
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager(), jaxbContext);
    }

    public JaxbServiceFactory(TransportManager transportManager)
    {
    	this(transportManager, null);
    }

	public JaxbServiceFactory(TransportManager transportManager, JAXBContext jaxbContext) {
        super(new Jsr181WebAnnotations(),
              transportManager, 
                new JaxbBindingProvider(new JaxbTypeRegistry(jaxbContext)));
        
        setWsdlBuilderFactory(new JaxbWSDLBuilderFactory());
    }    
}
