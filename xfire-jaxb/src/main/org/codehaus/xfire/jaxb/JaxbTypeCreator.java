package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: chris
 * Date: Aug 12, 2005
 * Time: 5:32:07 PM
 */
public class JaxbTypeCreator implements TypeCreator
{
    private TypeCreator nextCreator;
    private JAXBContext jaxbContext;
    private TypeMapping typeMapping;

    public JaxbTypeCreator(TypeCreator nextCreator, JAXBContext jaxbContext)
    {
        this.nextCreator = nextCreator;
        this.jaxbContext = jaxbContext;
    }

    public QName getElementName(Method m, int index)
    {
        return nextCreator.getElementName(m, index);
    }

    public Type createType(Method m, int index)
    {
        Class clazz = null;
        if (index > -1)
        {
            clazz = m.getParameterTypes()[index];
        } else
        {
            clazz = m.getReturnType();
        }

        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        } 
        else
        {
            return nextCreator.createType(m, index);
        }
    }

    public static boolean isJaxbType(Class clazz)
    {
        return Element.class.isAssignableFrom(clazz);
    }

    private Type createJaxbType(Class clazz)
    {
        return new JaxbType(clazz, jaxbContext);  //To change body of created methods use File | Settings | File Templates.
    }

    public Type createType(PropertyDescriptor pd)
    {
        Class clazz = pd.getPropertyType();
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        } else
        {
            return nextCreator.createType(pd);
        }
    }

    public Type createType(Field f)
    {
        Class clazz = f.getType();
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        } else
        {
            return nextCreator.createType(f);
        }
    }

    public Type createType(Class clazz)
    {
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        } else
        {
            return nextCreator.createType(clazz);
        }
    }

    public void setTypeMapping(TypeMapping typeMapping)
    {
        this.typeMapping = typeMapping;

        nextCreator.setTypeMapping(typeMapping);
    }

}
