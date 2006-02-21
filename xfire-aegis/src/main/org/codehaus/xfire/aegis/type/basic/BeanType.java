package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.NamespaceHelper;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 * Serializes JavaBeans.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:jack.xu.hong@gmail.com">Jack Hong</a>
 */
public class BeanType
    extends Type
{
    private BeanTypeInfo _info;
    private boolean isInterface = false;
    private boolean isException = false;
    
    public BeanType()
    {
        setNillable(true);
    }
    
    public BeanType(BeanTypeInfo info)
    {
        this._info = info;
        setNillable(true);
        
        this.setTypeClass(info.getTypeClass());
    }
	
	public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        BeanTypeInfo info = getTypeInfo();
        
        try
        {
            Class clazz = getTypeClass();
            Object object = null;
            InterfaceInvocationHandler delegate = null;
            
            if (isInterface)
            {
                delegate = new InterfaceInvocationHandler();
                object = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                                new Class[] { clazz },
                                                delegate);
            }
            else if (isException)
            {
                object = createFromFault(context);
            }
            else
            {
                object = clazz.newInstance();
            }

            // Read attributes
            while (reader.hasMoreAttributeReaders())
            {
                MessageReader childReader = reader.getNextAttributeReader();
                QName name = childReader.getName();
                
                Type type = info.getType(name);

                if (type != null)
                {
                    Object writeObj = type.readObject(childReader, context);

                    if (isInterface) {
                    	delegate.writeProperty(name.getLocalPart(), writeObj);
                    }
                    else {
                    	writeProperty(name, object, writeObj);
                    }
                }
            }

            // Read child elements
            while( reader.hasMoreElementReaders() )
            {
                MessageReader childReader = reader.getNextElementReader();
                QName name = childReader.getName();

                Type type = info.getType(name);

                if (type != null)
                {
                    if (!childReader.isXsiNil())
                    {
                        Object writeObj = type.readObject(childReader, context);

                        if (isInterface)
                        {
                            delegate.writeProperty(name.getLocalPart(), writeObj);
                        }
                        else
                        {
                            writeProperty(name, object, writeObj);
                        }
                    }
                    else
                    {
                        if (!info.isNillable(name))
                        {
                            throw new XFireFault(name.getLocalPart() + " is nil, but not nillable.",
                                    XFireFault.SENDER);

                        }
                        childReader.readToEnd();
                    }
                }
                else
                {
                    childReader.readToEnd();
                }
            }
            
            return object;
        }
        catch (IllegalAccessException e)
        {
            throw new XFireFault("Illegal access. " + e.getMessage(), e, XFireFault.RECEIVER);
        }
		catch (InstantiationException e)
		{
            throw new XFireFault("Couldn't instantiate class. " + e.getMessage(), e, XFireFault.SENDER);
		}
        catch (SecurityException e)
        {
            throw new XFireFault("Illegal access. " + e.getMessage(), e, XFireFault.RECEIVER);
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireFault("Illegal argument. " + e.getMessage(), e, XFireFault.RECEIVER);
        }
        catch (InvocationTargetException e)
        {
            throw new XFireFault("Couldn't create class: " + e.getMessage(), e, XFireFault.RECEIVER);
        }
    }

    /**
     * If the class is an exception, this will try and instantiate it with information
     * from the XFireFault (if it exists).
     */
    protected Object createFromFault(MessageContext context) 
        throws SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class clazz = getTypeClass();
        Constructor ctr;
        Object o;
        Object body = context.getExchange().getFaultMessage().getBody();

        if (!(body instanceof XFireFault)) 
            return clazz.newInstance();
        
        XFireFault fault = (XFireFault) body;

        try
        {
            ctr = clazz.getConstructor(new Class[] { String.class, Throwable.class });
            o = ctr.newInstance(new Object[] {fault.getMessage(), fault});
        }
        catch (NoSuchMethodException e)
        {
            try
            {
                ctr = clazz.getConstructor(new Class[] { String.class, Exception.class });
                o = ctr.newInstance(new Object[] {fault.getMessage(), fault});
            }
            catch (NoSuchMethodException e1)
            {
                try
                {
                    ctr = clazz.getConstructor(new Class[] { String.class });
                    o = ctr.newInstance(new Object[] {fault.getMessage()});
                }
                catch (NoSuchMethodException e2)
                {
                    return clazz.newInstance();
                }
            }
        }

        return o;
    }

    /**
     * Write the specified property to a field.
     */
    protected void writeProperty(QName name, Object object, Object property)
        throws XFireFault
    {
        try
        {
            PropertyDescriptor desc = getTypeInfo().getPropertyDescriptorFromMappedName(name);
            
            Method m = desc.getWriteMethod();
            
            if (m == null) throw new XFireFault("No write method for property " + name + " in " + object.getClass(), XFireFault.SENDER);

            Class propertyType = desc.getPropertyType();
            if ((property == null && !propertyType.isPrimitive()) || (property != null))
            {
                m.invoke(object, new Object[] { property });
            }
        }
        catch (Exception e)
        {
            throw new XFireFault("Couldn't set property " + name + " on " + object + ". " + e.getMessage(), e, XFireFault.SENDER);
        }
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeObject(Object, org.codehaus.xfire.aegis.MessageWriter, org.codehaus.xfire.MessageContext) 
     */
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;
        
        BeanTypeInfo info = getTypeInfo();
        
    	for (Iterator itr = info.getAttributes(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            Object value = readProperty(object, name);
            if (value != null)
            {
                Type type = getType( info, name );
                
                if ( type == null )
                    throw new XFireRuntimeException( "Couldn't find type for " + value.getClass() + " for property " + name );
    
                MessageWriter cwriter = writer.getAttributeWriter(name);

                type.writeObject(value, cwriter, context);
    
                cwriter.close();
            }
        }
        
        for (Iterator itr = info.getElements(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            Object value = readProperty(object, name);
            
            Type type = getType( info, name );
            MessageWriter cwriter;

            // Write the value if it is not null.
            if ( value != null)
            {
                cwriter = getWriter(writer, name, type);
                
                if ( type == null )
                    throw new XFireRuntimeException( "Couldn't find type for " + value.getClass() + " for property " + name );

                type.writeObject(value, cwriter, context);
                
                cwriter.close();
            }
            else if (info.isNillable(name))
            {
                cwriter = getWriter(writer, name, type);
                
                // Write the xsi:nil if it is null.
                cwriter.writeXsiNil();
                
                cwriter.close();
            }
        }
    }

    private MessageWriter getWriter(MessageWriter writer, QName name, Type type) 
    {
        MessageWriter cwriter;
        if (type.isAbstract())
        {
            cwriter = writer.getElementWriter(name);
        }
        else
        {
            cwriter = writer.getElementWriter(name);
        }
        return cwriter;
    }

    protected Object readProperty(Object object, QName name)
    {
        try
        {
            PropertyDescriptor desc = getTypeInfo().getPropertyDescriptorFromMappedName(name);

            Method m = desc.getReadMethod();
            
            if (m == null) throw new XFireFault("No read method for property " + name + " in class " + object.getClass().getName(), XFireFault.SENDER);

            return m.invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException( "Couldn't get property " + name + " from bean " + object, e );
        }
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeSchema(org.jdom.Element) 
     */
    public void writeSchema(Element root)
    {
        BeanTypeInfo info = getTypeInfo();
        
        Element complex = new Element("complexType",
                                      SoapConstants.XSD_PREFIX,
                                      SoapConstants.XSD);
        complex.setAttribute(new Attribute("name", getSchemaType().getLocalPart()));
        root.addContent(complex);

        Element seq = null;
        
        // Write out schema for elements
        for (Iterator itr = info.getElements(); itr.hasNext();)
        {
            if (seq == null)
            {
                seq = new Element("sequence", SoapConstants.XSD_PREFIX, SoapConstants.XSD);
                complex.addContent(seq);
            }
                            
            QName name = (QName) itr.next();
            
            Element element = new Element("element",
                                          SoapConstants.XSD_PREFIX,
                                          SoapConstants.XSD);
            seq.addContent(element);
            
            Type type = getType(info, name);
            
            String nameNS = name.getNamespaceURI(); 
            String nameWithPrefix = getNameWithPrefix(root, nameNS, name.getLocalPart());
            
            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            type.getSchemaType().getNamespaceURI() );
            
            writeTypeReference(name, nameWithPrefix, element, type, prefix);
        }
        
        // Write out schema for attributes
        for (Iterator itr = info.getAttributes(); itr.hasNext();)
        {
            QName name = (QName) itr.next();
            
            Element element = new Element("attribute",
                                          SoapConstants.XSD_PREFIX,
                                          SoapConstants.XSD);
            complex.addContent(element);
            
            Type type = getType(info, name);

            String nameNS = name.getNamespaceURI(); 
            String nameWithPrefix = getNameWithPrefix(root, nameNS, name.getLocalPart());

            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            type.getSchemaType().getNamespaceURI() );
            element.setAttribute(new Attribute("name", nameWithPrefix));
            element.setAttribute(new Attribute("type", prefix + ':' + type.getSchemaType().getLocalPart()));
        }
    }

    private String getNameWithPrefix(Element root, String nameNS, String localName)
    {
        if (!nameNS.equals(getSchemaType().getNamespaceURI()))
        {
            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), nameNS);
            if (prefix != null && prefix.length() > 0) 
                return prefix + ":" + localName;
        }
        return localName;
    }

    private Type getType(BeanTypeInfo info, QName name)
    {
        Type type = info.getType(name);
        
        if (type == null)
        {
            throw new NullPointerException("Couldn't find type for" + name + " in class " + 
                                           getTypeClass().getName());
        }
        
        return type;
    }

    private void writeTypeReference(QName name, String nameWithPrefix, Element element, Type type, String prefix)
    {
        if (type.isAbstract())
        {
            element.setAttribute(new Attribute("name", nameWithPrefix));
            element.setAttribute(new Attribute("type", prefix + ':' + type.getSchemaType().getLocalPart()));
            element.setAttribute(new Attribute("minOccurs", "0"));
            
            if (getTypeInfo().isNillable(name))
            {
                element.setAttribute(new Attribute("nillable", "true"));
            }
        }
        else
        {
            element.setAttribute(new Attribute("ref", prefix + ':' + type.getSchemaType().getLocalPart()));
        }
    }
    
    public void setTypeClass(Class typeClass)
    {
        super.setTypeClass(typeClass);
        
        isInterface = typeClass.isInterface();
        isException = Exception.class.isAssignableFrom(typeClass);
    }

    /**
     * We need to write a complex type schema for Beans, so return true.
     * 
     * @see org.codehaus.xfire.aegis.type.Type#isComplex()
     */
    public boolean isComplex()
    {
        return true;
    }

    public Set getDependencies()
    {
        Set deps = new HashSet();

        BeanTypeInfo info = getTypeInfo();
        
        for (Iterator itr = info.getAttributes(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            deps.add(info.getType(name));
        }

        for (Iterator itr = info.getElements(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            deps.add(info.getType(name));
        }
        
        return deps;
    }

    public BeanTypeInfo getTypeInfo()
    {
        if (_info == null)
        {
            _info = createTypeInfo();
        }
        
        // Delay initialization so things work in recursive scenarios (XFIRE-117)
        if (!_info.isInitialized())
        {
            _info.initialize();
        }
        
        return _info;
    }

    public BeanTypeInfo createTypeInfo()
    {
        BeanTypeInfo info = new BeanTypeInfo(getTypeClass(), 
                                             getSchemaType().getNamespaceURI());

        info.setTypeMapping(getTypeMapping());

        return info;
    }
}
