package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

public class XMLBeanTypeInfo
    extends BeanTypeInfo
{
    private static final Log logger = LogFactory.getLog(XMLBeanTypeInfo.class);
    private String encodingUri;
    private Element mapping;
    private QName name;
    
    public XMLBeanTypeInfo(TypeMapping tm, 
                           Class typeClass,
                           Element mapping)
    {
        super(typeClass, tm.getEncodingStyleURI());

        this.mapping = mapping;
        setTypeMapping(tm);
    }

    public QName getSchemaType()
    {
        if (name == null)
        {
            name = createQName(mapping, mapping.getAttributeValue("name"));
        }
        
        return name;
    }

    protected void mapProperty(PropertyDescriptor pd)
    {
        Element e = getPropertyElement(mapping, pd.getName());
        String style = null;
        QName name = null;
        
        if (e != null)
        {
            String ignore = e.getAttributeValue("ignore");
            if (ignore != null && ignore.equals("true"))
                return;
            
            logger.debug("Found mapping for property " + pd.getName());

            style = e.getAttributeValue("style");
            name = createQName(e, e.getAttributeValue("mappedName"));
        }
        
        if (style == null) style = "element";
        if (name == null) name = createQName(pd);
        
        try
        {
            Type type = getTypeMapping().getTypeCreator().createType(pd);

            getTypeMapping().register(type);

            if (style.equals("element"))
                mapElement(pd.getName(), name);
            else if (style.equals("attribute"))
                mapAttribute(pd.getName(), name);
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

    private Element getPropertyElement(Element mapping2, String name2)
    {
        Elements elements = mapping2.getChildElements("property");
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            String name = e.getAttributeValue("name");
            
            if (name != null && name.equals(name2))
            {
                return e;
            }
        }
        
        return null;
    }

    private Class loadClass(String componentType)
    {
        try
        {
            return ClassLoaderUtils.loadClass(componentType, getClass());
        }
        catch (ClassNotFoundException e)
        {
            throw new XFireRuntimeException("Couldn't find component type: " + componentType, e);
        }
    }

    protected QName createQName(Element e, String value)
    {
        if (value == null) return null;
        
        int index = value.indexOf(":");
        
        if (index == -1)
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        
        String prefix = value.substring(0, index);
        String localName = value.substring(index+1);
        String ns = e.getNamespaceURI(prefix);
        
        if (ns == null || localName == null)
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        
        return new QName(ns, localName, prefix);
    }
}
