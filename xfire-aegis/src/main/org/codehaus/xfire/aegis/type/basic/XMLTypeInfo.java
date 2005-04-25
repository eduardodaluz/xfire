package org.codehaus.xfire.aegis.type.basic;

import java.beans.PropertyDescriptor;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.type.collection.CollectionType;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;

public class XMLTypeInfo
    extends TypeInfo
{
    private static final Log logger = LogFactory.getLog(XMLTypeInfo.class);
    private String encodingUri;
    private InputStream is;
    
    public static final String MAPPING_NS = "urn:xfire:aegis:200504";
    
    public XMLTypeInfo(String encodingUri, 
                       Class typeClass,
                       InputStream is)
    {
        super(typeClass);
        
        this.encodingUri = encodingUri;
        this.is = is;
    }
    
    public void initialize()
    {
        try
        {
            buildType(is);
        }
        catch (XMLStreamException e)
        {
            logger.error("Couldn't parse type descriptor!", e);
            throw new XFireRuntimeException("Couldn't parse type descriptor!", e);
        }
    }

    protected void buildType(InputStream is) throws XMLStreamException
    {
        Document doc = new StaxBuilder().build(is);
        Element mapping = getMapping(encodingUri, 
                                     doc.getRootElement().getChildElements());
        
        if (mapping == null)
        {
            logger.warn("Couldn't find mapping for class " + getClass().getName() + " with uri of " + encodingUri);
            
            super.initialize();
        }
        else
        {
            buildMapping(mapping);
        }
    }

    protected void buildMapping(Element mapping)
    {
        Elements elements = mapping.getChildElements();

        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            
            if (e.getLocalName().equals("attribute"))
                mapAttribute(e);
            else if (e.getLocalName().equals("element"))
                mapElement(e);
            else if (e.getLocalName().equals("collection"))
                mapCollection(e);
        }
    }

    protected void mapElement(Element e)
    {
        String property = e.getAttributeValue("property");
        logger.debug("Mapping element for property " + property);
        
        PropertyDescriptor pd = getPropertyDescriptor(property);
        if (pd == null)
            throw new XFireRuntimeException("Invalid property: " + property);
        
        QName name = createQName(e, e.getAttributeValue("name"));
        
        if (name == null)
            name = createQName(pd);
        
        mapElement(property, name);
    }

    protected void mapAttribute(Element e)
    {
        String property = e.getAttributeValue("property");
        logger.debug("Mapping attribute for property " + property);
        
        PropertyDescriptor pd = getPropertyDescriptor(property);
        if (pd == null)
            throw new XFireRuntimeException("Invalid property: " + property);
        
        QName name = createQName(e, e.getAttributeValue("name"));
        
        if (name == null)
            name = createQName(pd);
        
        mapAttribute(property, name);
    }

    protected void mapCollection(Element e)
    {
        String property = e.getAttributeValue("property");
        logger.debug("Mapping collection for property " + property);
        
        PropertyDescriptor pd = getPropertyDescriptor(property);
        if (pd == null)
            throw new XFireRuntimeException("Invalid property: " + property);
        
        QName name = createQName(e, e.getAttributeValue("name"));
        
        if (name == null)
            name = createQName(pd);
        
        String componentType = e.getAttributeValue("componentType");
        if (componentType == null)
            throw new XFireRuntimeException("Component type cannot be empty!");
        
        CollectionType type = new CollectionType(loadClass(componentType));
        type.setSchemaType(name);
        type.setTypeMapping(getTypeMapping());
        type.setTypeClass(pd.getPropertyType());
        
        getTypeMapping().register(type);
        
        mapElement(property, name);
    }

    private Class loadClass(String componentType)
    {
        try
        {
            return getClass().getClassLoader().loadClass(componentType);
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
                if (!mapping.getLocalName().equals("mapping") || 
                    !mapping.getNamespaceURI().equals(MAPPING_NS))
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
            if (!mapping.getLocalName().equals("mapping") || 
                !mapping.getNamespaceURI().equals(MAPPING_NS))
                break;
            
            if (mapping.getAttribute("uri") == null)
                return mapping;
        }
        
        return null;
    }
}
