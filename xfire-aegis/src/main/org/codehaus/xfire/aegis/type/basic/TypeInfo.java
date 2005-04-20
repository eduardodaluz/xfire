package org.codehaus.xfire.aegis.type.basic;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;

public class TypeInfo
{
    private Map qname2name = new HashMap();
    private Class typeClass;
    private List attributes = new ArrayList();
    private List elements = new ArrayList();
    private String defaultNamespace;
    private QName schemaType;
    private PropertyDescriptor[] descriptors;

    public TypeInfo(Class typeClass, QName complexType)
    {
        this.typeClass = typeClass;
        this.defaultNamespace = complexType.getNamespaceURI();
        
        initializeProperties();
    }

    protected TypeInfo(Class typeClass)
    {
        this.typeClass = typeClass;

        initializeProperties();
    }

    protected PropertyDescriptor[] getPropertyDescriptors()
    {
        return descriptors;
    }

    protected PropertyDescriptor getPropertyDescriptor(String name)
    {
        for (int i = 0; i < descriptors.length; i++)
        {
            if (descriptors[i].getName().equals(name));
                return descriptors[i];
        }
        
        return null;
    }
    
    public void initialize()
    {
        try
        {
            for (int i = 0; i < descriptors.length; i++)
            {
                String name = descriptors[i].getName();

                if (isAttribute(descriptors[i]))
                {
                    mapAttribute(name, createQName(descriptors[i]));
                }
                else if (isElement(descriptors[i]))
                {
                    mapElement(name, createQName(descriptors[i]));
                }
            }
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't create TypeInfo.", e);
        }
    }

    protected QName createQName(PropertyDescriptor desc)
    {
        return new QName(defaultNamespace, desc.getName());
    }

    public void mapAttribute(String property, QName type)
    {
        qname2name.put(type, property);
        attributes.add(type);
    }

    public void mapElement(String property, QName type)
    {
        qname2name.put(type, property);
        elements.add(type);
    }
    
    private void initializeProperties()
    {
        BeanInfo beanInfo = null;
        try
        {
            if (typeClass.isInterface())
            {
                beanInfo = Introspector.getBeanInfo(typeClass);
            }
            else
            {
                beanInfo = Introspector.getBeanInfo(typeClass, Object.class);
            }
        }
        catch (IntrospectionException e)
        {
            throw new XFireRuntimeException("Couldn't introspect interface.", e);
        }
        
        descriptors = beanInfo.getPropertyDescriptors();
        
        if (descriptors == null)
        {
            descriptors = new PropertyDescriptor[0];
        }
    }

    public PropertyDescriptor getPropertyDescriptor(QName name)
    {
        String propName = getPropertyName(name);
        for (int i = 0; i < descriptors.length; i++)
        {
            if (descriptors[i].getName().equals(propName))
                return descriptors[i];
        }
        
        return null;
    }
    
    protected boolean isAttribute(PropertyDescriptor desc)
    {
        return false;
    }
    
    protected boolean isElement(PropertyDescriptor desc)
    {
        return true;
    }

    protected boolean isSerializable(PropertyDescriptor desc)
    {
        return true;
    }

    protected Class getTypeClass()
    {
        return typeClass;
    }

    public boolean isNillable(QName name)
    {
        return true;
    }
    
    private String getPropertyName(QName name)
    {
        return (String) qname2name.get(name);
    }
    
    public Iterator getAttributes()
    {
        return attributes.iterator();
    }

    public Iterator getElements()
    {
        return elements.iterator();
    }
}