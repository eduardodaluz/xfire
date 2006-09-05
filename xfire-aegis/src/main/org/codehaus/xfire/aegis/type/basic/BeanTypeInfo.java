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
import org.codehaus.xfire.aegis.type.TypeCreator;
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

    private String defaultNamespace;

    private int minOccurs = 0;

    private boolean nillable = true;

    private boolean isExtension = false;

    /**
     * extensibleElements means adding xs:any to WSDL Complex Type Definition
     */
    private boolean extensibleElements = true;

    /**
     * extensibleAttributes means adding xs:anyAttribute to WSDL Complex Type
     * Definition
     */
    private boolean extensibleAttributes = true;

    public BeanTypeInfo(Class typeClass, String defaultNamespace)
    {
        this.beanClass = typeClass;
        this.defaultNamespace = defaultNamespace;

        initializeProperties();
    }

    /**
     * Create a BeanTypeInfo class.
     * 
     * @param typeClass
     * @param defaultNamespace
     * @param initiallize
     *            If true attempt default property/xml mappings.
     */
    public BeanTypeInfo(Class typeClass, String defaultNamespace, boolean initialize)
    {
        this.beanClass = typeClass;
        this.defaultNamespace = defaultNamespace;

        initializeProperties();
        setInitialized(!initialize);
    }

    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }

    public void initialize()
    {
        try
        {
            for (int i = 0; i < descriptors.length; i++)
            {
                // Don't map the property unless there is a read property
                if (isMapped(descriptors[i]))
                {
                    mapProperty(descriptors[i]);
                }
            }
        }
        catch (Exception e)
        {
            if (e instanceof XFireRuntimeException)
                throw (XFireRuntimeException) e;
            throw new XFireRuntimeException("Couldn't create TypeInfo.", e);
        }

        setInitialized(true);
    }

    public boolean isMapped(PropertyDescriptor pd)
    {
        if (pd.getReadMethod() == null)
            return false;

        return true;
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
    public Type getType(QName name)
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

                if (type != null)
                    mapType(name, type);
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
                if (e instanceof XFireRuntimeException)
                    throw (XFireRuntimeException) e;
                throw new XFireRuntimeException("Couldn't get properties.", e);
            }

            if (desc == null)
            {
                return null;
            }

            try
            {
                TypeMapping tm = getTypeMapping();
                TypeCreator tc = tm.getTypeCreator();
                type = tc.createType(desc);
            }
            catch (XFireRuntimeException e)
            {
                e.prepend("Couldn't create type for property " + desc.getName() + " on "
                        + getTypeClass());

                throw e;
            }

            // second part is possible workaround for XFIRE-586
            if (registerType(desc))
                getTypeMapping().register(type);

            mapType(name, type);
        }

        if (type == null)
            throw new XFireRuntimeException("Couldn't find type for property " + name);

        return type;
    }

    protected boolean registerType(PropertyDescriptor desc)
    {
        return true;
    }

    public void mapType(QName name, Type type)
    {
        mappedName2type.put(name, type);
    }

    private QName getMappedTypeName(QName name)
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
     * Specifies the name of the property as it shows up in the xml schema. This
     * method just returns <code>propertyDescriptor.getName();</code>
     * 
     * @param desc
     * @return
     */
    protected QName createMappedName(PropertyDescriptor desc)
    {
        return new QName(getDefaultNamespace(), desc.getName());
    }

    public void mapAttribute(String property, QName mappedName)
    {
        mappedName2pdName.put(mappedName, property);
        attributes.add(mappedName);
    }

    public void mapElement(String property, QName mappedName)
    {
        mappedName2pdName.put(mappedName, property);
        elements.add(mappedName);
    }

    /**
     * Specifies the SchemaType for a particular class.
     * 
     * @param mappedName
     * @param type
     */
    public void mapTypeName(QName mappedName, QName type)
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
                descriptors = getInterfacePropertyDescriptors(beanClass);
            }
            else if (beanClass == Object.class || beanClass == Throwable.class)
            {
            }
            else if (beanClass == Throwable.class)
            {
            }
            else if (Throwable.class.isAssignableFrom(beanClass))
            {
                beanInfo = Introspector.getBeanInfo(beanClass, Throwable.class);
            }
            else if (RuntimeException.class.isAssignableFrom(beanClass))
            {
                beanInfo = Introspector.getBeanInfo(beanClass, RuntimeException.class);
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

    public PropertyDescriptor[] getInterfacePropertyDescriptors(Class clazz)
    {
        try
        {
            Class[] interfaces = clazz.getInterfaces();
            List list = new ArrayList();
            /**
             * add base interface information
             */
            BeanInfo info = Introspector.getBeanInfo(clazz);
            for (int j = 0; j < info.getPropertyDescriptors().length; j++)
            {
                list.add(info.getPropertyDescriptors()[j]);
            }
            /**
             * add extended interface information
             */
            for (int i = 0; i < interfaces.length; i++)
            {
                BeanInfo superInfo = Introspector.getBeanInfo(interfaces[i]);

                for (int j = 0; j < superInfo.getPropertyDescriptors().length; j++)
                {
                    list.add(superInfo.getPropertyDescriptors()[j]);
                }
            }
            return (PropertyDescriptor[]) list.toArray(new PropertyDescriptor[list.size()]);
        }
        catch (IntrospectionException e)
        {
            return null;
        }
    }

    public PropertyDescriptor getPropertyDescriptorFromMappedName(QName name)
    {
        return getPropertyDescriptor(getPropertyNameFromMappedName(name));
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

    /**
     * Nillable is only allowed if the actual property is Nullable
     * 
     * @param name
     * @return
     */
    public boolean isNillable(QName name)
    {
        Type type = getType(name);
        if (!type.isNillable())
            return false;
        return nillable;
    }

    public int getMinOccurs(QName name)
    {
        return minOccurs;
    }

    public void setDefaultMinOccurs(int minOccurs)
    {
        this.minOccurs = minOccurs;
    }

    public void setDefaultNillable(boolean nillable)
    {
        this.nillable = nillable;
    }

    private String getPropertyNameFromMappedName(QName name)
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

    public boolean isExtensibleElements()
    {
        return extensibleElements;
    }

    public void setExtensibleElements(boolean futureProof)
    {
        this.extensibleElements = futureProof;
    }

    public boolean isExtensibleAttributes()
    {
        return extensibleAttributes;
    }

    public void setExtensibleAttributes(boolean extensibleAttributes)
    {
        this.extensibleAttributes = extensibleAttributes;
    }

    public void setExtension(boolean extension)
    {
        this.isExtension = extension;
    }

    public boolean isExtension()
    {
        return isExtension;
    }

}