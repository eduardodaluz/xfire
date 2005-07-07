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
        
        buildMapping(mapping);
    }

    public QName getSchemaType()
    {
        if (name == null)
        {
            name = createQName(mapping, mapping.getAttributeValue("name"));
        }
        
        return name;
    }
    
    protected void buildMapping(Element mapping)
    {
        Elements elements = mapping.getChildElements();

        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            
            String ignore = e.getAttributeValue("ignore");
            if (ignore == null || !ignore.equals("true"))
            {
                mapElement(e);
            }
        }
    }

    protected void mapElement(Element e)
    {
        String property = e.getAttributeValue("name");
        logger.debug("Mapping element for property " + property);
        
        PropertyDescriptor pd = getPropertyDescriptor(property);
        
        try
        {
            Type type = getTypeMapping().getTypeCreator().createType(pd);

            getTypeMapping().register(type);

            String style = e.getAttributeValue("style");
            if (style == null) style = "element";
            
            QName name = createQName(e, e.getAttributeValue("mappedName"));
            if (name == null) name = createQName(pd);
            
            if (style.equals("element"))
                mapElement(property, name);
            else if (style.equals("attribute"))
                mapAttribute(property, name);
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
