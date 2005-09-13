package org.codehaus.xfire.aegis.type.basic;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;

public class BeanTypeInfo
{
    private Map qname2name = new HashMap();
    private Class typeClass;
    private List attributes = new ArrayList();
    private List elements = new ArrayList();
    private String defaultNamespace;
    private PropertyDescriptor[] descriptors;
    private TypeMapping typeMapping;
    private boolean initialized;
    
    public BeanTypeInfo(Class typeClass, String defaultNamespace)
    {
        this.typeClass = typeClass;
        this.defaultNamespace = defaultNamespace;
        
        initializeProperties();
    }

    /**
     * Create a BeanTypeInfo class.
     * 
     * @param typeClass
     * @param defaultNamespace
     * @param initiallize If true attempt default property/xml mappings.
     */
    public BeanTypeInfo(Class typeClass, String defaultNamespace, boolean initialize)
    {
        this.typeClass = typeClass;
        this.defaultNamespace = defaultNamespace;
        
        initializeProperties();
        setInitialized(!initialize);
    }
    
    protected BeanTypeInfo(Class typeClass)
    {
        this.typeClass = typeClass;

        initializeProperties();
    }
    
    public void initialize()
    {
        try
        {
            for (int i = 0; i < descriptors.length; i++)
            {
                // Don't map the property unless there is both a read and write property
                if (descriptors[i].getReadMethod() != null &&
                        descriptors[i].getWriteMethod() != null)
                {
                    mapProperty(descriptors[i]);
                }
            }
        }
        catch (Exception e)
        {
            if(e instanceof XFireRuntimeException) throw (XFireRuntimeException)e;
            throw new XFireRuntimeException("Couldn't create TypeInfo.", e);
        }
        
        setInitialized(true);
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    private void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    protected void mapProperty(PropertyDescriptor pd)
    {
        String name = pd.getName();
   
        if (isAttribute(pd))
        {
            mapAttribute(name, createQName(pd));
        }
        else if (isElement(pd))
        {
            mapElement(name, createQName(pd));
        }
    }
    
    protected PropertyDescriptor[] getPropertyDescriptors()
    {
        return descriptors;
    }

    protected PropertyDescriptor getPropertyDescriptor(String name)
    {
        for (int i = 0; i < descriptors.length; i++)
        {
            if (descriptors[i].getName().equals(name))
                return descriptors[i];
        }
        
        return null;
    }

    /**
     * Get the type class for the field with the specified QName.
     */
    public Type getType(QName name) 
    {
        Type type = getTypeMapping().getType(name);
        
        if (type == null)
        {
            PropertyDescriptor desc;
            try
            {
                desc = getPropertyDescriptor(name);
            }
            catch (Exception e)
            {
                if(e instanceof XFireRuntimeException) throw (XFireRuntimeException)e;
                throw new XFireRuntimeException("Couldn't get properties.", e);
            }
            
            if (desc == null)
            {
                return null;
            }

            if (getTypeMapping().isRegistered(desc.getPropertyType()) 
                    && !Collection.class.isAssignableFrom(desc.getPropertyType()))
            {
                type = getTypeMapping().getType(desc.getPropertyType());
            }
            else
            {
                try
                {
                    type = getTypeMapping().getTypeCreator().createType(desc);
                }
                catch(XFireRuntimeException e)
                {
                    e.prepend("Couldn't create type for property " + desc.getName() 
                              + " on " + getTypeClass());
                    
                    throw e;
                }
                
                getTypeMapping().register(type);
            }
        }
        
        if ( type == null )
            throw new XFireRuntimeException( "Couldn't find type for property " + name );
        
        return type;
    }

    public TypeMapping getTypeMapping()
    {
        return typeMapping;
    }

    public void setTypeMapping(TypeMapping typeMapping)
    {
        this.typeMapping = typeMapping;
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
            if (typeClass.isInterface() || typeClass.isPrimitive())
            {
                beanInfo = Introspector.getBeanInfo(typeClass);
            }
            else if (typeClass == Object.class)
            {
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
        
        if (beanInfo != null)
            descriptors = beanInfo.getPropertyDescriptors();
        
        if (descriptors == null)
        {
            descriptors = new PropertyDescriptor[0];
        }
    }

    public PropertyDescriptor getPropertyDescriptor(QName name)
    {
        return getPropertyDescriptor( getPropertyName(name) );
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