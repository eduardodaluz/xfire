package org.codehaus.xfire.plexus.config;

import java.lang.reflect.Constructor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.commons.CommonsWebAttributes;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.TypeMappingRegistry;

public class AnnotationServiceConfigurator
    extends ObjectServiceConfigurator
{
    public ServiceFactory getServiceFactory(PlexusConfiguration config)
        throws Exception
    {
        Class annotsClz = null;
        Class clz = null;
        String annots = config.getChild("annotations").getValue();
        String factoryClass = config.getChild("serviceFactory").getValue();
        
        if (annots == null)
        {
            annotsClz = CommonsWebAttributes.class;
        }
        else
        {
            annotsClz = loadClass(annots);
        }
        
        if (factoryClass == null)
        {
            clz = AnnotationServiceFactory.class;
        }
        else
        {
            clz = loadClass(factoryClass);
        }
        
        Constructor con = 
            clz.getConstructor( new Class[] {WebAnnotations.class, TransportManager.class, TypeMappingRegistry.class} );
        
        return (ServiceFactory) 
            con.newInstance(new Object[] {annotsClz.newInstance(), getXFire().getTransportManager(), getTypeMappingRegistry()});
    }
}
