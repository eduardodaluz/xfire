package org.codehaus.xfire.aegis.yom;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.aegis.AbstractMessageWriter;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

public class YOMWriter
    extends AbstractMessageWriter
{
    private Element element;
    
    public YOMWriter(Element element)
    {
        this.element = element;
    }
    
    public void writeValue(Object value)
    {
        element.appendChild(value.toString());
    }

    public void writeValue(Object value, String ns, String attr)
    {
        element.addAttribute(new Attribute(attr, ns, value.toString()));
    }

    public MessageWriter getElementWriter(String name)
    {
        return getElementWriter(name, element.getNamespaceURI());
    }

    public MessageWriter getElementWriter(String name, String namespace)
    {
        String prefix = NamespaceHelper.getUniquePrefix(element, namespace);
        
        Element child = new Element(prefix + ":" + name, namespace);
        element.appendChild(child);
        
        return new YOMWriter(child);
    }

    public MessageWriter getElementWriter(QName qname)
    {
        String name = qname.getLocalPart();
        if (qname.getPrefix().length() > 0)
        {
            name = qname.getPrefix() + ":" + name;
        }
        else
        {
            String prefix = NamespaceHelper.getUniquePrefix(element, qname.getNamespaceURI());
            name = prefix + ":" + name;
        }
        
        Element child = new Element(name, qname.getNamespaceURI());
        element.appendChild(child);
        
        return new YOMWriter(child);
    }

    public XMLStreamWriter getXMLStreamWriter()
    {
        throw new UnsupportedOperationException("Stream writing not supported from a YOMWriter.");
    }

    public MessageWriter getAttributeWriter(String name)
    {
        Attribute att = new Attribute(element.getNamespacePrefix() + ":" + name,
                                      element.getNamespaceURI(),
                                      "");
        element.addAttribute(att);
        return new AttributeWriter(att);
    }

    public MessageWriter getAttributeWriter(String name, String namespace)
    {
        Attribute att = null;
        if (namespace != null && namespace.length() > 0)
        {
            String prefix = NamespaceHelper.getUniquePrefix(element, namespace);
            att = new Attribute(prefix + ":" + name, namespace, "");
        }
        else
        {
            att = new Attribute(name, "");
        }

        element.addAttribute(att);
        return new AttributeWriter(att);
    }

    public MessageWriter getAttributeWriter(QName qname)
    {
        return getAttributeWriter(qname.getLocalPart(), qname.getNamespaceURI());
    }

    public void close()
    {
    }
}
