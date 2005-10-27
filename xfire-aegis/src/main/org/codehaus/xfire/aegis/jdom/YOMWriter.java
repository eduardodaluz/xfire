package org.codehaus.xfire.aegis.jdom;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.aegis.AbstractMessageWriter;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.util.NamespaceHelper;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

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
        element.addContent(value.toString());
    }

    public void writeValue(Object value, String ns, String attr)
    {
        element.setAttribute(new Attribute(attr, value.toString(), Namespace.getNamespace(ns)));
    }

    public MessageWriter getElementWriter(String name)
    {
        return getElementWriter(name, element.getNamespaceURI());
    }

    public MessageWriter getElementWriter(String name, String namespace)
    {
        String prefix = NamespaceHelper.getUniquePrefix(element, namespace);
        
        Element child = new Element(name, Namespace.getNamespace(prefix, namespace));
        element.addContent(child);
        
        return new YOMWriter(child);
    }

    public MessageWriter getElementWriter(QName qname)
    {
        Element child = new Element(qname.getLocalPart(), 
                                    Namespace.getNamespace(qname.getPrefix(),
                                                           qname.getNamespaceURI()));
        element.addContent(child);
        
        return new YOMWriter(child);
    }

    public XMLStreamWriter getXMLStreamWriter()
    {
        throw new UnsupportedOperationException("Stream writing not supported from a YOMWriter.");
    }

    public MessageWriter getAttributeWriter(String name)
    {
        Attribute att = new Attribute(name,
                                      "",
                                      Namespace.getNamespace(element.getNamespacePrefix(), 
                                                             element.getNamespaceURI()));
        element.setAttribute(att);
        return new AttributeWriter(att);
    }

    public MessageWriter getAttributeWriter(String name, String namespace)
    {
        Attribute att = null;
        if (namespace != null && namespace.length() > 0)
        {
            String prefix = NamespaceHelper.getUniquePrefix(element, namespace);
            att = new Attribute(name, "", Namespace.getNamespace(prefix, namespace));
        }
        else
        {
            att = new Attribute(name, "");
        }

        element.setAttribute(att);
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
