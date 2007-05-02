package org.codehaus.xfire.jaxb2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.Configuration;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * A BindingProvider for the Jaxb type system.
 * 
 * XFIRE-868
 * also add Jaxbtypes to the overriding types to pass to the wsdl builder
 * 
 * @author <a href="mailto:tom.lambrechts@telindus.be">Tom Lambrechts</a>
 */
public class JaxbBindingProvider
    extends AegisBindingProvider
{
    public JaxbBindingProvider()
    {
        super();
    }

    public JaxbBindingProvider(TypeCreator creator)
    {
        super(creator);
    }
    
    public JaxbBindingProvider(TypeCreator creator, Configuration config)
    {
        super(creator, config);
        
    }
    public JaxbBindingProvider(TypeMappingRegistry registry)
    {
        super(registry);
    }
    
    public void initialize(Service service)
    {
        super.initialize(service);
        
        List classes = (List) service.getProperty(OVERRIDE_TYPES_KEY);

        if (classes != null)
        {
            List types = null;
            types = (List) service.getProperty(WSDLBuilder.OVERRIDING_TYPES);
            if(types == null)
                types = new ArrayList();
            
            TypeMapping tm = getTypeMapping(service);
            for (Iterator it = classes.iterator(); it.hasNext();)
            {
                String typeName = (String) it.next();
                Class c;
                try
                {
                    c = ClassLoaderUtils.loadClass(typeName, JaxbBindingProvider.class);
                }
                catch (ClassNotFoundException e)
                {
                    throw new XFireRuntimeException("Could not find override type class: " + typeName, e);
                }
                
                Type t = tm.getType(c);
                if (t instanceof JaxbType)
                {
                     JaxbType bt = (JaxbType) t;
                    types.add(bt);
                }
            }
            service.setProperty(WSDLBuilder.OVERRIDING_TYPES, types);
        }
    }
}