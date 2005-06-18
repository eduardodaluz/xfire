package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

public class XMLBeanTypeInfo
    extends BeanTypeInfo
{
    private static final Log logger = LogFactory.getLog(XMLBeanTypeInfo.class);
    private String encodingUri;
    private Element mapping;

    public XMLBeanTypeInfo(String encodingUri, 
                       Class typeClass,
                       Element mapping)
    {
        super(typeClass, encodingUri);

        this.mapping = mapping;

        buildMapping(mapping);
    }

    protected void buildMapping(Element mapping)
    {
        Elements elements = mapping.getChildElements();

        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            
            mapElement(e);
        }
    }

    protected void mapElement(Element e)
    {
        String property = e.getAttributeValue("name");
        logger.debug("Mapping element for property " + property);
        
        PropertyDescriptor pd = getPropertyDescriptor(property);
        if (pd == null)
            throw new XFireRuntimeException("Invalid property: " + property);
        
        QName name = createQName(e, e.getAttributeValue("mappedName"));
        
        if (name == null)
            name = createQName(pd);
        
        String style = e.getAttributeValue("style");
        if (style == null) style = "element";
        
        if (style.equals("element"))
            mapElement(property, name);
        else if (style.equals("attribute"))
            mapAttribute(property, name);
        else
            throw new XFireRuntimeException("Invalid style: " + style);
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
    
    protected Element getMapping(String uri, Elements mappings)
    {
        // Check for a mapping with the specified Uri
        if (uri != null && uri.length() > 0)
        {
            for (int i = 0; i < mappings.size(); i++)
            {
                Element mapping = mappings.get(i);
                if (!mapping.getLocalName().equals("mapping"))
                    break;
                
                String uriValue = mapping.getAttributeValue("uri");
                if (uriValue != null && uriValue.equals(uri))
                    return mapping;
            }
            
            logger.debug("No mapping for " + uri + ". Trying default mapping.");
        }
        
        // Check for a mapping without a uri.
        for (int i = 0; i < mappings.size(); i++)
        {
            Element mapping = mappings.get(i);
            if (!mapping.getLocalName().equals("mapping"))
                break;
            
            if (mapping.getAttribute("uri") == null)
                return mapping;
        }
        
        return null;
    }
}
