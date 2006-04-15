package org.codehaus.xfire.jaxb2;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;

public class JaxbTypeCreator
    implements TypeCreator
{
    private TypeCreator nextCreator;
    private TypeMapping typeMapping;
    
    public JaxbTypeCreator(TypeCreator nextCreator)
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
        
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        }
        else
        {
            return nextCreator.createType(m, index);
        }
    }

    protected Type createJaxbType(Class clazz)
    {
        JaxbType type = new JaxbType(clazz);
        type.getSchemaType();
        return type;
    }

    @SuppressWarnings("unchecked")
    public static boolean isJaxbType(Class clazz)
    {
        return clazz.isAnnotationPresent(XmlElement.class)
                || clazz.isAnnotationPresent(XmlType.class)
                || clazz.isAnnotationPresent(XmlRootElement.class);
    }

    public Type createType(PropertyDescriptor pd)
    {
        Class clazz = pd.getPropertyType();
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
        }
        else
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
        }
        else
        {
            return nextCreator.createType(f);
        }
    }

    public Type createType(Class clazz)
    {
        if (isJaxbType(clazz))
        {
            return createJaxbType(clazz);
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
