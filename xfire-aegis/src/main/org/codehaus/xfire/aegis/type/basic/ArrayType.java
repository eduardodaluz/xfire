package org.codehaus.xfire.aegis.type.basic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * An ArrayType.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ArrayType
    extends Type
{
    private QName componentName;
    
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            Type compType = getComponentType();
            
            List values = new ArrayList();
            
            while ( reader.hasMoreElementReaders() )
            {
                values.add( compType.readObject(reader.getNextElementReader(), context) );
            }
            
            return makeArray(getComponentType().getTypeClass(), values);
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }

    protected Object makeArray(Class arrayType, List values)
    {
        if (Integer.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Integer.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Long.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Long.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Short.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Short.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Double.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Double.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Float.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Float.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Byte.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Byte.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        else if (Boolean.TYPE.equals(arrayType))
        {
            Object[] objects = values.toArray();
            Object array = Array.newInstance(Boolean.TYPE, objects.length);
            for (int i = 0, n = objects.length; i < n; i++)
            {
                Array.set(array, i, objects[i]);
            }
            return array;
        }
        
        return values.toArray( (Object[]) Array.newInstance( getComponentType().getTypeClass(), 
                                                             values.size()) );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;

        try
        {
        	Object[] array = (Object[]) object;
            
            Type type = getComponentType();
            
            if ( type == null )
                throw new XFireRuntimeException( "Couldn't find type for " + type.getTypeClass() + "." );
            
            for ( int i = 0; i < array.length; i++ )
            {
                String ns = null;
                if (type.isAbstract())
                    ns = getSchemaType().getNamespaceURI();
                else
                    ns = type.getSchemaType().getNamespaceURI();

                MessageWriter cwriter = writer.getElementWriter(type.getSchemaType().getLocalPart(),
                                                                ns);
                type.writeObject( array[i], writer, context );
                cwriter.close();
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }

    public void writeSchema(Element root)
    {
        try
        {
            Element complex = new Element(SoapConstants.XSD_PREFIX + ":complexType",
                                          SoapConstants.XSD);
            complex.addAttribute(new Attribute("name", getSchemaType().getLocalPart()));
            root.appendChild(complex);

            Element seq = new Element(SoapConstants.XSD_PREFIX + ":sequence",
                                      SoapConstants.XSD);
            complex.appendChild(seq);
            
            Element element = new Element(SoapConstants.XSD_PREFIX + ":element",
                                      SoapConstants.XSD);
            seq.appendChild(element);

            Type componentType = getComponentType();
            String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                            componentType.getSchemaType().getNamespaceURI());

            String typeName = prefix + ":"
                    + componentType.getSchemaType().getLocalPart();

            element.addAttribute(new Attribute("name", componentType.getSchemaType().getLocalPart()));
            element.addAttribute(new Attribute("type", typeName));
            element.addAttribute(new Attribute("nillable", "true"));

            element.addAttribute(new Attribute("minOccurs", "0"));
            element.addAttribute(new Attribute("maxOccurs", "unbounded"));
            
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
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
    
    public QName getComponentName()
    {
        return componentName;
    }
    

    public void setComponentName(QName componentName)
    {
        this.componentName = componentName;
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#getDependencies()
     */
    public Set getDependencies()
    {
        Set deps = new HashSet();
        
        deps.add( getComponentType() );
        
        return deps;
    }
    
    /**
     * Get the <code>Type</code> of the elements in the array.
     * 
     * @return
     */
    public Type getComponentType()
    {
        Class compType = getTypeClass().getComponentType();
        
        if (componentName == null)
            return getTypeMapping().getType(compType);
        else
            return getTypeMapping().getType(componentName);
    }
}
