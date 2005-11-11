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
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;

public class BeanTypeInfo
{
    private Map mappedName2typeName = new HashMap();
    private Map mappedName2pdName = new HashMap();
    private Map mappedName2type = new HashMap();

    private Class beanClass;
    private List attributes = new ArrayList();
    private List elements = new ArrayList();
    private PropertyDescriptor[] descriptors;
    private TypeMapping typeMapping;
    private boolean initialized;
    
    public BeanTypeInfo(Class typeClass)
    {
        this.beanClass = typeClass;

        initializeProperties();
    }

    /**
     * Create a BeanTypeInfo class.
     * 
     * @param typeClass
     * @param defaultNamespace
     * @param initiallize If true attempt default property/xml mappings.
     */
    public BeanTypeInfo(Class typeClass, boolean initialize)
    {
        this.beanClass = typeClass;

        initializeProperties();
        setInitialized(!initialize);
    }

    public void initialize()
    {
        try
        {
            for (int i = 0; i < descriptors.length; i++)
            {
                // Don't map the property unless there is a read property
                if (descriptors[i].getReadMethod() != null)
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
            mapAttribute(name, createMappedName(pd));
        }
        else if (isElement(pd))
        {
            mapElement(name, createMappedName(pd));
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
    public Type getType(String name) 
    {
        // 1. Try a prexisting mapped type
        Type type = (Type) mappedName2type.get(name);

        // 2. Try to get the type by its name, if there is one
        if (type == null)
        {
            QName typeName = getMappedTypeName(name);
            if (typeName != null)
            {
                type = getTypeMapping().getType(typeName);
                
                if (type != null) mapType(name, type);
            }
        }
        
        // 3. Create the type from the property descriptor and map it
        if (type == null)
        {
            PropertyDescriptor desc;
            try
            {
                desc = getPropertyDescriptorFromMappedName(name);
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
            
            if (registerType(desc)) getTypeMapping().register(type);
            
            mapType(name, type);
        }
        
        if ( type == null )
            throw new XFireRuntimeException( "Couldn't find type for property " + name );
        
        return type;
    }

    protected boolean registerType(PropertyDescriptor desc)
    {
        return true;
    }

    public void mapType(String name, Type type)
    {
        mappedName2type.put(name, type);
    }

    private QName getMappedTypeName(String name)
    {
        return (QName) mappedName2typeName.get(name);
    }

    public TypeMapping getTypeMapping()
    {
        return typeMapping;
    }

    public void setTypeMapping(TypeMapping typeMapping)
    {
        this.typeMapping = typeMapping;
    }

    /**
     * Specifies the name of the property as it shows up in the xml schema.
     * This method just returns <code>propertyDescriptor.getName();</code>
     * @param desc
     * @return
     */
    protected String createMappedName(PropertyDescriptor desc)
    {
        return desc.getName();
    }

    public void mapAttribute(String property, String mappedName)
    {
        mappedName2pdName.put(mappedName, property);
        attributes.add(mappedName);
    }

    public void mapElement(String property, String mappedName)
    {
        mappedName2pdName.put(mappedName, property);
        elements.add(mappedName);
    }
    
    /**
     * Specifies the SchemaType for a particular class.
     * @param mappedName
     * @param type
     */
    public void mapTypeName(String mappedName, QName type)
    {
        mappedName2typeName.put(mappedName, type);
    }
    
    private void initializeProperties()
    {
        BeanInfo beanInfo = null;
        try
        {
            if (beanClass.isInterface() || beanClass.isPrimitive())
            {
                beanInfo = Introspector.getBeanInfo(beanClass);
            }
            else if (beanClass == Object.class)
            {
            }
            else if(beanClass == Throwable.class)
            {
            }
            else if (Throwable.class.isAssignableFrom(beanClass))
            {
                beanInfo = Introspector.getBeanInfo(beanClass, Throwable.class);
            }
            else
            {
                beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
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

    public PropertyDescriptor getPropertyDescriptorFromMappedName(String name)
    {
        return getPropertyDescriptor( getPropertyNameFromMappedName(name) );
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
        return beanClass;
    }

    public boolean isNillable(String name)
    {
        return getType(name).isNillable();
    }
    
    private String getPropertyNameFromMappedName(String name)
    {
        return (String) mappedName2pdName.get(name);
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