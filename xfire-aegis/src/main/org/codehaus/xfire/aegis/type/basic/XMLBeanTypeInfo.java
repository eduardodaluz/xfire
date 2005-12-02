package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.jdom.Element;

public class XMLBeanTypeInfo
    extends BeanTypeInfo
{
    private static final Log logger = LogFactory.getLog(XMLBeanTypeInfo.class);
    private List mappings;
    private Element topMapping;
    private QName name;
    private Map name2Nillable = new HashMap();
 
    public XMLBeanTypeInfo(Class typeClass,
                           Element mapping,
                           List mappings,
                           String defaultNamespace)
    {
        super(typeClass, defaultNamespace);

        this.mappings = mappings;
        this.topMapping = mapping;
    }

    public QName getSchemaType()
    {
        if (name == null && topMapping != null)
        {
            name = createQName(topMapping, topMapping.getAttributeValue("name"), getDefaultNamespace());
        }
        
        return name;
    }

    protected boolean registerType(PropertyDescriptor desc)
    {
        Element e = getPropertyElement(desc.getName());
        if (e != null && e.getAttributeValue("type") != null) return false;
        
        return super.registerType(desc);
    }

    protected void mapProperty(PropertyDescriptor pd)
    {
        Element e = getPropertyElement(pd.getName());
        String style = null;
        QName mappedName = null;
        
        if (e != null)
        {
            String ignore = e.getAttributeValue("ignore");
            if (ignore != null && ignore.equals("true"))
                return;
            
            logger.debug("Found mapping for property " + pd.getName());

            style = e.getAttributeValue("style");
            mappedName = createQName(e, e.getAttributeValue("mappedName"), getDefaultNamespace());
        }
        
        if (style == null) style = "element";
        if (mappedName == null) mappedName = createMappedName(pd);
        
        if (e != null)
        {
            QName mappedType = createQName(e, e.getAttributeValue("typeName"), getDefaultNamespace());
            if (mappedType != null) mapTypeName(mappedName, mappedType);
            
            String nillableVal = e.getAttributeValue("nillable");
            if (nillableVal != null && nillableVal.length() > 0)
            {
                 name2Nillable.put(mappedName, Boolean.valueOf(nillableVal));
            }
        }

        try
        {
            //logger.debug("Mapped " + pd.getName() + " as " + style + " with name " + mappedName);
            if (style.equals("element"))
                mapElement(pd.getName(), mappedName);
            else if (style.equals("attribute"))
                mapAttribute(pd.getName(), mappedName);
            else
                throw new XFireRuntimeException("Invalid style: " + style);
        }
        catch(XFireRuntimeException ex)
        {
            ex.prepend("Couldn't create type for property " + pd.getName() 
                      + " on " + getTypeClass());
            
            throw ex;
        }
    }

    private Element getPropertyElement(String name2)
    {
        for (Iterator itr = mappings.iterator(); itr.hasNext();)
        {
            Element mapping2 = (Element) itr.next();
            List elements = mapping2.getChildren("property");
            for (int i = 0; i < elements.size(); i++)
            {
                Element e = (Element) elements.get(i);
                String name = e.getAttributeValue("name");
                
                if (name != null && name.equals(name2))
                {
                    return e;
                }
            }
        }
        
        return null;
    }

    protected QName createQName(Element e, String value, String defaultNamespace)
    {
        if (value == null) return null;
        
        int index = value.indexOf(":");
        
        if (index == -1)
        {
            return new QName(defaultNamespace, value);
        }
        
        String prefix = value.substring(0, index);
        String localName = value.substring(index+1);
        String ns = e.getNamespace(prefix).getURI();
        
        if (ns == null || localName == null)
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        
        return new QName(ns, localName, prefix);
    }

    public boolean isNillable(QName name)
    {
        Boolean nillable = (Boolean) name2Nillable.get(name);
        
        if (nillable != null) return nillable.booleanValue();
        
        return super.isNillable(name);
    } 
}
