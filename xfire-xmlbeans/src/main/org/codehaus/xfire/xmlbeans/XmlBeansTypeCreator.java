package org.codehaus.xfire.xmlbeans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;

public class XmlBeansTypeCreator
    implements TypeCreator
{
    private TypeCreator nextCreator;
    private TypeMapping typeMapping;
    
    public XmlBeansTypeCreator(TypeCreator nextCreator)
    {
        this.nextCreator = nextCreator;   
    }
    
    public QName getElementName(Method m, int index)
    {
        return nextCreator.getElementName(m, index);
    }

    public TypeMapping getTypeMapping()
    {
        return typeMapping;
    }

    public Type createType(Method m, int index)
    {
        Class clazz = null;
        if (index > -1)
        {
            clazz = m.getParameterTypes()[index];
        }
        else
        {
            clazz = m.getReturnType();
        }
        
        if (isXmlBean(clazz))
        {
            return createXmlBeanType(clazz);
        }
        else
        {
            return nextCreator.createType(m, index);
        }
    }

    protected Type createXmlBeanType(Class clazz)
    {
        return new XmlBeansType(clazz);
    }

    private boolean isXmlBean(Class clazz)
    {
        return XmlObject.class.isAssignableFrom(clazz);
    }

    public Type createType(PropertyDescriptor pd)
    {
        Class clazz = pd.getPropertyType();
        if (isXmlBean(clazz))
        {
            return createXmlBeanType(clazz);
        }
        else
        {
            return nextCreator.createType(pd);
        }
    }

    public Type createType(Field f)
    {
        Class clazz = f.getType();
        if (isXmlBean(clazz))
        {
            return createXmlBeanType(clazz);
        }
        else
        {
            return nextCreator.createType(f);
        }
    }

    public Type createType(Class clazz)
    {
        if (isXmlBean(clazz))
        {
            return createXmlBeanType(clazz);
        }
        else
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
