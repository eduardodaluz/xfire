package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log logger = LogFactory.getLog(BeanType.class);
    
    private static Map objectProperties = null;
    
    private BeanTypeInfo _info;
    
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
            Object object = clazz.newInstance();

            // Read attributes
            while (reader.hasMoreAttributeReaders())
            {
                MessageReader childReader = reader.getNextAttributeReader();
                QName name = childReader.getName();
                
                Type type = info.getType(name);

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
                
                Type type = info.getType(name);

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
     * @see org.codehaus.xfire.aegis.type.Type#writeObject(java.lang.Object)
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
            MessageWriter cwriter = writer.getElementWriter(name);

            if ( value != null)
            {
                Type type = getType( info, name );
    
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
            throw new XFireRuntimeException( "Couldn't get property " + name, e );
        }
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeSchema()
     */
    public void writeSchema(Element root)
    {
        BeanTypeInfo info = getTypeInfo();
        
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
            
            Type type = getType(info, name);
            
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
            
            Type type = getType(info, name);

            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            type.getSchemaType().getNamespaceURI() );
            
            element.addAttribute(new Attribute("name", name.getLocalPart()));
            element.addAttribute(new Attribute("type", prefix + ":" + type.getSchemaType().getLocalPart()));
            
            if (info.isNillable(name))
            {
                element.addAttribute(new Attribute("nillable", "true"));
            }
        }
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

    private void writeTypeReference(QName name, Element element, Type type, String prefix)
    {
        if (type.isAbstract())
        {
            element.addAttribute(new Attribute("name", name.getLocalPart()));
            element.addAttribute(new Attribute("type", prefix + ":" + type.getSchemaType().getLocalPart()));
            
            if (type.isNillable())
            {
                element.addAttribute(new Attribute("nillable", "true"));
            }
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
        BeanTypeInfo info = new BeanTypeInfo(getTypeClass(), getSchemaType().getNamespaceURI());

        info.setTypeMapping(getTypeMapping());

        return info;
    }
}