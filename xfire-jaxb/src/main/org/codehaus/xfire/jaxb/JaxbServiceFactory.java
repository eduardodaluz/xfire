package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class JaxbServiceFactory
        extends ObjectServiceFactory
{
    JaxbIntrospector jaxbIntrospector;

    public JaxbServiceFactory(TransportManager transportManager, JAXBContext jaxbContext)
    {
        super(transportManager);
        jaxbIntrospector = new JaxbIntrospector(jaxbContext);
        AegisBindingProvider provider = new JaxbBindingProvider(new JaxbTypeRegistry(jaxbContext));
        setBindingProvider(provider);
    }

    protected QName getInParameterName(Service service, Method method, int paramNumber, boolean doc)
    {
        Class[] paramClasses = method.getParameterTypes();
        if (JaxbTypeCreator.isJaxbType(paramClasses[paramNumber]))
        {
            return jaxbIntrospector.introspect(paramClasses[paramNumber]);
        } else
        {
            return super.getInParameterName(service, method, paramNumber, doc);
        }
    }

    protected QName getOutParameterName(Service service, Method method, boolean doc)
    {
        Class clazz = method.getReturnType();
        if (JaxbTypeCreator.isJaxbType(clazz))
        {
            return jaxbIntrospector.introspect(clazz);
        } else
        {
            return super.getOutParameterName(service, method, doc);
        }
    }
}
