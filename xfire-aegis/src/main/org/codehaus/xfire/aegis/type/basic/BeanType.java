package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

/**
 * Serializes JavaBeans.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class BeanType
    extends Type
{
    private static Map objectProperties = null;
    
    private TypeInfo info;
    
    public BeanType()
    {
    }
    
    public BeanType(TypeInfo info)
    {
        this.info = info;
        
        this.setTypeClass(info.getTypeClass());
    }

    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        TypeInfo info = getTypeInfo();
        
        try
        {
            Class clazz = getTypeClass();
            Object object = clazz.newInstance();

            // Read attributes
            while (reader.hasMoreAttributeReaders())
            {
                MessageReader childReader = reader.getNextAttributeReader();
                QName name = childReader.getName();
                
                Type type = getType(name);

                if (type != null)
                {
                    Object writeObj = type.readObject(childReader, context);
    
                    writeProperty(name, object, writeObj);
                }
            }

            // Read child elements
            while( reader.hasMoreElementReaders() )
            {
                MessageReader childReader = reader.getNextElementReader();
                QName name = childReader.getName();
                
                Type type = getType(name);

                if (type != null)
                {
                    Object writeObj = type.readObject(childReader, context);
    
                    writeProperty(name, object, writeObj);
                }
                else
                {
                    readToEnd(childReader);
                }
            }
            
            return object;
        }
        catch (IllegalAccessException e)
        {
            throw new XFireFault("Illegal access.", e, XFireFault.RECEIVER);
        }
		catch (InstantiationException e)
		{
            throw new XFireFault("Couldn't instantiate service.", e, XFireFault.SENDER);
		}
    }

    private void readToEnd(MessageReader childReader)
    {
        while (childReader.hasMoreElementReaders())
        {
            readToEnd(childReader.getNextElementReader());
        }
    }

    /**
     * Write the specified property to a field.
     */
    protected void writeProperty(QName name, Object object, Object property)
        throws XFireFault
    {
        try
        {
            PropertyDescriptor desc = getTypeInfo().getPropertyDescriptor(name);
            desc.getWriteMethod().invoke(object, new Object[] {property});
        }
        catch (Exception e)
        {
            throw new XFireFault("Couldn't set property " + name, e, XFireFault.SENDER);
        }
    }

    /**
     * Get the type class for the field with the specified QName.
     * @param object
     * @param name
     * @param namespace
     * @return
     * @throws XFireFault
     */
    protected Type getType(QName name) 
    {
        Type type = getTypeMapping().getType(name);
        
        if (type == null)
        {
            PropertyDescriptor desc = null;
            try
            {
                desc = getTypeInfo().getPropertyDescriptor(name);
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't get properties.", e);
            }
            
            if (desc == null)
            {
                return null;
            }
            
            type = getTypeMapping().getType(desc.getPropertyType());
        }
        
        if ( type == null )
            throw new XFireRuntimeException( "Couldn't find type for property " + name );
        
        return type;
    }


    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeObject(java.lang.Object)
     */
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;
        
        TypeInfo info = getTypeInfo();
        
    	for (Iterator itr = info.getAttributes(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            Object value = readProperty(object, name);
            if (value != null)
            {
                Type type = getTypeMapping().getType( value.getClass() );
    
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
            MessageWriter cwriter = writer.getElementWriter(name);

            if ( value != null)
            {
                Type type = getTypeMapping().getType( value.getClass() );
    
                if ( type == null )
                    throw new XFireRuntimeException( "Couldn't find type for " + value.getClass() + " for property " + name );
    
                type.writeObject(value, cwriter, context);
            }
            else if (info.isNillable(name))
            {
                MessageWriter attWriter = cwriter.getAttributeWriter("nil", SoapConstants.XSI_NS);
                attWriter.writeValue("true");
                attWriter.close();
            }
            
            cwriter.close();
        }
    }

    protected Object readProperty(Object object, QName name)
    {
        try
        {
            PropertyDescriptor desc = getTypeInfo().getPropertyDescriptor(name);
            return desc.getReadMethod().invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException( "Couldn't get property " + name );
        }
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeSchema()
     */
    public void writeSchema(Element root)
    {
        TypeInfo info = getTypeInfo();
        
        Element complex = new Element(SoapConstants.XSD_PREFIX + ":complexType",
                                      SoapConstants.XSD);
        complex.addAttribute(new Attribute("name", getSchemaType().getLocalPart()));
        root.appendChild(complex);

        Element seq = null;
        
        for (Iterator itr = info.getElements(); itr.hasNext();)
        {
            if (seq == null)
            {
                seq = new Element(SoapConstants.XSD_PREFIX + ":sequence", SoapConstants.XSD);
                complex.appendChild(seq);
            }
                            
            QName name = (QName) itr.next();
            
            Element element = new Element(SoapConstants.XSD_PREFIX + ":element",
                                          SoapConstants.XSD);
            seq.appendChild(element);
            
            Type type = getType(name);
            
            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            type.getSchemaType().getNamespaceURI() );
            
            writeTypeReference(name, element, type, prefix);
        }
        
        for (Iterator itr = info.getAttributes(); itr.hasNext();)
        {
            QName name = (QName) itr.next();
            
            Element element = new Element(SoapConstants.XSD_PREFIX + ":attribute",
                                          SoapConstants.XSD);
            complex.appendChild(element);
            
            Type type = getType(name);
            
            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            type.getSchemaType().getNamespaceURI() );
            
            element.addAttribute(new Attribute("name", name.getLocalPart()));
            element.addAttribute(new Attribute("type", prefix + ":" + type.getSchemaType().getLocalPart()));
            
            if (info.isNillable(name))
                element.addAttribute(new Attribute("nillable", "true"));
        }
    }

    private void writeTypeReference(QName name, Element element, Type type, String prefix)
    {
        if (type.isAbstract())
        {
            element.addAttribute(new Attribute("name", name.getLocalPart()));
            element.addAttribute(new Attribute("type", prefix + ":" + type.getSchemaType().getLocalPart()));
            
            if (info.isNillable(name))
                element.addAttribute(new Attribute("nillable", "true"));
        }
        else
        {
            element.addAttribute(new Attribute("ref", prefix + ":" + type.getSchemaType().getLocalPart()));
        }
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

        TypeInfo info = getTypeInfo();
        
        for (Iterator itr = info.getAttributes(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            deps.add(getType(name));
        }

        for (Iterator itr = info.getElements(); itr.hasNext(); )
        {
            QName name = (QName) itr.next();

            deps.add(getType(name));
        }
        
        return deps;
    }

    public TypeInfo getTypeInfo()
    {
        if (info == null)
            info = createTypeInfo();
        
        return info;
    }

    public TypeInfo createTypeInfo()
    {
        TypeInfo info = new TypeInfo(getTypeClass(), getSchemaType());
        
        info.initialize();
        
        return info;
    }
}