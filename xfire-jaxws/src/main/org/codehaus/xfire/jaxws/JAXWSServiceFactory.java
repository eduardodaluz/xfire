package org.codehaus.xfire.jaxws;

import java.lang.reflect.Method;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

public class JAXWSServiceFactory
    extends AnnotationServiceFactory
{

    public JAXWSServiceFactory(TransportManager transportManager)
    {
        super(new Jsr181WebAnnotations(), 
              transportManager, 
              new AegisBindingProvider(new JaxbTypeRegistry()));
    }

    @Override
    protected OperationInfo addOperation(Service endpoint, Method method, String style)
    {
        OperationInfo op = super.addOperation(endpoint, method, style);
        
        return op;
    }

    
}
