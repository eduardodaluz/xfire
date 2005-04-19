package org.codehaus.xfire.aegis.yom;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.aegis.AbstractMessageReader;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.stax.AttributeReader;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

public class YOMReader
    extends AbstractMessageReader
    implements MessageReader
{
    private Element element;
    private int currentChild = 0;
    private int currentAttribute = 0;
    private Elements elements;
    private QName qname;
    
    public YOMReader(Element element)
    {
        this.element = element;
        this.elements = element.getChildElements();
    }
    
    public String getValue()
    {
        return element.getValue();
    }

    public String getValue(String ns, String attr)
    {
        return element.getAttributeValue(attr, ns);
    }

    public boolean hasMoreElementReaders()
    {
        return (currentChild < elements.size());
    }

    public MessageReader getNextElementReader()
    {
        currentChild++;
        return new YOMReader(elements.get(currentChild-1));
    }

    public QName getName()
    {
        if (qname == null)
        {
            qname = new QName(element.getNamespaceURI(), 
                              element.getLocalName(), 
                              element.getNamespacePrefix());
        }
        return qname;
    }

    public String getLocalName()
    {
        return element.getLocalName();
    }

    public String getNamespace()
    {
        return element.getNamespaceURI();
    }

    public XMLStreamReader getXMLStreamReader()
    {
        throw new UnsupportedOperationException("Stream reading not supported from a YOMWriter.");
    }

    public boolean hasMoreAttributeReaders()
    {
        return (currentAttribute < element.getAttributeCount());
    }

    public MessageReader getNextAttributeReader()
    {
        currentAttribute++;
        Attribute att = element.getAttribute(currentAttribute);
        
        return new AttributeReader(new QName(att.getNamespaceURI(), att.getLocalName()), att.getValue());
    }
}
