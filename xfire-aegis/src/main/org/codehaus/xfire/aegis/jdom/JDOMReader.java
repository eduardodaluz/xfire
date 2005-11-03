package org.codehaus.xfire.aegis.jdom;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.aegis.AbstractMessageReader;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.stax.AttributeReader;
import org.jdom.Attribute;
import org.jdom.Element;

public class JDOMReader
    extends AbstractMessageReader
    implements MessageReader
{
    private Element element;
    private int currentChild = 0;
    private int currentAttribute = 0;
    private List elements;
    private QName qname;
    
    public JDOMReader(Element element)
    {
        this.element = element;
        this.elements = element.getChildren();
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
        return new JDOMReader((Element) elements.get(currentChild-1));
    }

    public QName getName()
    {
        if (qname == null)
        {
            qname = new QName(element.getNamespaceURI(), 
                              element.getName(), 
                              element.getNamespacePrefix());
        }
        return qname;
    }

    public String getLocalName()
    {
        return element.getName();
    }

    public String getNamespace()
    {
        return element.getNamespaceURI();
    }

    public XMLStreamReader getXMLStreamReader()
    {
        throw new UnsupportedOperationException("Stream reading not supported from a JDOMWriter.");
    }

    public boolean hasMoreAttributeReaders()
    {
        return (currentAttribute < element.getAttributes().size());
    }

    public MessageReader getNextAttributeReader()
    {
        currentAttribute++;
        Attribute att = (Attribute) element.getAttributes().get(currentAttribute);
        
        return new AttributeReader(new QName(att.getNamespaceURI(), att.getName()), att.getValue());
    }
}