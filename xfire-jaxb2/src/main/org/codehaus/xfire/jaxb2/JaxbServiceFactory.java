package org.codehaus.xfire.jaxb2;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class JaxbServiceFactory
        extends ObjectServiceFactory
{
    public JaxbServiceFactory()
    {
        AegisBindingProvider provider = new AegisBindingProvider(new JaxbTypeRegistry());
        provider.setWriteOuter(false);
        setBindingProvider(provider);
        
        // setWsdlBuilder(XmlBeansWSDLBuilder.class);
    }

    public JaxbServiceFactory(TransportManager transportManager)
    {
        super(transportManager);
        
        AegisBindingProvider provider = new AegisBindingProvider(new JaxbTypeRegistry());
        provider.setWriteOuter(false);
        setBindingProvider(provider);
        // setWsdlBuilder(XmlBeansWSDLBuilder.class);
    }

    protected QName getInParameterName(Service service, Method method, int paramNumber, boolean doc)
    {
        Class[] paramClasses = method.getParameterTypes();
        if (JaxbTypeCreator.isJaxbType(paramClasses[paramNumber]))
        {
            return JaxbType.getSchemaType(paramClasses[paramNumber]);
        }
        else
        {
            return super.getInParameterName(service, method, paramNumber, doc);
        }
    }

    protected QName getOutParameterName(Service service, Method method, boolean doc)
    {
        Class clazz = method.getReturnType();
        if (JaxbTypeCreator.isJaxbType(clazz))
        {
            return JaxbType.getSchemaType(clazz);
        }
        else
        {
            return super.getOutParameterName(service, method, doc);
        }
    }
}
