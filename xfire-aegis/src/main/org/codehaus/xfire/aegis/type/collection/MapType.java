package org.codehaus.xfire.aegis.type.collection;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

public class MapType
    extends Type
{
    private Class keyClass;
    private Class valueClass;
    private QName keyName;
    private QName valueName;
    private QName entryName;
    
    public MapType(QName schemaType, Class keyClass, Class valueClass)
    {
        super();
        
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        
        setSchemaType(schemaType);
        setKeyName(new QName(schemaType.getNamespaceURI(), "key"));
        setValueName(new QName(schemaType.getNamespaceURI(), "value"));
        setEntryName(new QName(schemaType.getNamespaceURI(), "entry"));
    }

    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        Map map = instantiateMap();
        try
        {
            Type keyType = getKeyType();
            Type valueType = getValueType();

            Object key = null;
            Object value = null;
            
            while (reader.hasMoreElementReaders())
            {
                MessageReader entryReader = reader.getNextElementReader();
                
                if (entryReader.getName().equals(getEntryName()))
                {
                    while (entryReader.hasMoreElementReaders())
                    {
                        MessageReader evReader = entryReader.getNextElementReader();
                       
                        if (evReader.getName().equals(getKeyName()))
                        {
                            key = keyType.readObject(evReader, context);
                        }
                        else if (evReader.getName().equals(getValueName()))
                        {
                            value = valueType.readObject(evReader, context);
                        }
                        else
                        {
                            readToEnd(evReader);
                        }
                    }
                    
                    map.put(key, value);
                }
                else
                {
                    readToEnd(entryReader);
                }
            }
            
            return map;
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
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
     * Creates a map instance. If the type class is a <code>Map</code> or extends
     * the <code>Map</code> interface a <code>HashMap</code> is created. Otherwise
     * the map classs (i.e. LinkedHashMap) is instantiated using the default constructor.
     * 
     * @return
     */
    protected Map instantiateMap()
    {
        Map map = null;
        
        if (getTypeClass().equals(Map.class) || getTypeClass().isInterface())
        {
            map = new HashMap();
        }
        else
        {
            try
            {
                map = (Map) getTypeClass().newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException(
                    "Could not create map implementation: " + getTypeClass().getName(), e);
            }
        }
        
        return map;
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;
    
        try
        {
            Map map = (Map) object;

            Type keyType = getKeyType();
            Type valueType = getValueType();

            if (keyType == null)
                throw new XFireRuntimeException("Couldn't find type for key class " 
                                                + keyType.getTypeClass() + ".");

            if (valueType == null)
                throw new XFireRuntimeException("Couldn't find type for value class " 
                                                + keyType.getTypeClass() + ".");

            
            for (Iterator itr = map.entrySet().iterator(); itr.hasNext();)
            {
                Map.Entry entry = (Map.Entry) itr.next();
                
                MessageWriter entryWriter = writer.getElementWriter(getEntryName());

                MessageWriter keyWriter = entryWriter.getElementWriter(getKeyName());
                keyType.writeObject(entry.getKey(), keyWriter, context);
                keyWriter.close();
                
                MessageWriter valueWriter = entryWriter.getElementWriter(getValueName());
                valueType.writeObject(entry.getValue(), valueWriter, context);
                valueWriter.close();
                
                entryWriter.close();
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }    

    public void writeSchema(Element root)
    {
        String ctPref = SoapConstants.XSD_PREFIX + ":complexType";
        String seqPref = SoapConstants.XSD_PREFIX + ":sequence";
        
        Element complex = new Element(ctPref, SoapConstants.XSD);
        complex.addAttribute(new Attribute("name", getSchemaType().getLocalPart()));
        root.appendChild(complex);

        Element seq = new Element(seqPref, SoapConstants.XSD);
        complex.appendChild(seq);

        Type keyType = getKeyType();
        Type valueType = getValueType();
        
        String prefix = NamespaceHelper.getUniquePrefix((Element) root.getParent(), 
                                                        getSchemaType().getNamespaceURI());

        String keyTypeName = prefix + ":" + keyType.getSchemaType().getLocalPart();
        String valueTypeName = prefix + ":" + valueType.getSchemaType().getLocalPart();

        Element element = new Element(SoapConstants.XSD_PREFIX + ":element", SoapConstants.XSD);
        seq.appendChild(element);

        element.addAttribute(new Attribute("name", getEntryName().getLocalPart()));
        element.addAttribute(new Attribute("minOccurs", "0"));
        element.addAttribute(new Attribute("maxOccurs", "unbounded"));
        
        Element evComplex = new Element(ctPref, SoapConstants.XSD);
        element.appendChild(evComplex);
        
        Element evseq = new Element(seqPref, SoapConstants.XSD);
        evComplex.appendChild(evseq);
        
        createElement(evseq, getKeyName(), keyTypeName);
        createElement(evseq, getValueName(), valueTypeName);
    }

    private void createElement(Element seq, QName name, String keyTypeName)
    {
        Element element = new Element(SoapConstants.XSD_PREFIX + ":element", SoapConstants.XSD);
        seq.appendChild(element);

        element.addAttribute(new Attribute("name", name.getLocalPart()));
        element.addAttribute(new Attribute("type", keyTypeName));

        element.addAttribute(new Attribute("minOccurs", "0"));
        element.addAttribute(new Attribute("maxOccurs", "1"));
    }

    public Type getKeyType()
    {
        return getOrCreateType(keyClass);
    }

    private Type getOrCreateType(Class clazz)
    {
        Type type = getTypeMapping().getType(clazz);
        if (type == null)
        {
            System.out.println("Couldn't find type for " + clazz);
            type = getTypeMapping().getTypeCreator().createType(clazz);
            getTypeMapping().register(type);
        }
        return type;
    }
    
    public Type getValueType()
    {
        return getOrCreateType(valueClass);
    }

    public Class getKeyClass()
    {
        return keyClass;
    }

    public void setKeyClass(Class keyClass)
    {
        this.keyClass = keyClass;
    }

    public QName getKeyName()
    {
        return keyName;
    }

    public void setKeyName(QName keyName)
    {
        this.keyName = keyName;
    }

    public Class getValueClass()
    {
        return valueClass;
    }

    public void setValueClass(Class valueClass)
    {
        this.valueClass = valueClass;
    }

    public QName getValueName()
    {
        return valueName;
    }

    public void setValueName(QName valueName)
    {
        this.valueName = valueName;
    }

    public QName getEntryName()
    {
        return entryName;
    }

    public void setEntryName(QName entryName)
    {
        this.entryName = entryName;
    }
}