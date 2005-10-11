package org.codehaus.xfire.jaxb;

import com.sun.xml.bind.RIElement;
import org.codehaus.xfire.XFireRuntimeException;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;

public class JaxbIntrospector
{
    private JAXBContext jaxbContext;

    public JaxbIntrospector(JAXBContext jaxbContext)
    {
        this.jaxbContext = jaxbContext;
    }

    public QName introspect(Class clazz)
    {
        String typeName = clazz.getName();
        typeName = typeName.substring(typeName.lastIndexOf('.') + 1);

        Method creator;
        // we need to create an instance of the jaxb bean to find out the qname
        RIElement instance;
        try
        {
            // use reflection to call create<TypeName>
            creator = jaxbContext.getClass().getMethod("create" + typeName, null);
            instance = (RIElement) creator.invoke(jaxbContext, null);

        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("error getting qname of jaxb type", e);
        }
        return new QName(instance.____jaxb_ri____getNamespaceURI(), 
                         instance.____jaxb_ri____getLocalName());
    }
}
