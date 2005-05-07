package org.codehaus.xfire.xmlbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceFactory
        extends ObjectServiceFactory
{
    public XMLBeansServiceFactory()
    {
    }

    public XMLBeansServiceFactory(TransportManager transportManager)
    {
        super(transportManager, new XMLBeansBindingProvider());
    }

    protected QName getInParameterName(ServiceEndpoint service, Method method, int paramNumber, boolean doc)
    {
        Class[] paramClasses = method.getParameterTypes();
        if (XmlObject.class.isAssignableFrom(paramClasses[paramNumber]))
        {
            return getSchemaType(paramClasses[paramNumber]).getDocumentElementName();
        }
        else
        {
            return super.getInParameterName(service, method, paramNumber, doc);
        }
    }

    protected QName getOutParameterName(ServiceEndpoint service, Method method, boolean doc)
    {
        if (XmlObject.class.isAssignableFrom(method.getReturnType()))
        {
            return getSchemaType(method.getReturnType()).getDocumentElementName();
        }
        else
        {
            return super.getOutParameterName(service, method, doc);
        }
    }

    /**
     * Introspect to find the SchemaType for a particular XMLBeans class.
     */
    protected SchemaType getSchemaType(Class clazz)
    {
        try
        {
            Field f = clazz.getDeclaredField("type");

            return (SchemaType) f.get(null);
        }
        catch (NoSuchFieldException e)
        {
            throw new XFireRuntimeException("Couldn't find type field!", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Couldn't get type field!", e);
        }
        catch (IllegalAccessException e)
        {
            throw new XFireRuntimeException("Couldn't get type field!", e);
        }
    }
}
