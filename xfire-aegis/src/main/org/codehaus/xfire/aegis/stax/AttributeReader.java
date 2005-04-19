package org.codehaus.xfire.aegis.stax;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractMessageReader;
import org.codehaus.xfire.aegis.MessageReader;

public class AttributeReader
    extends AbstractMessageReader
{
    private QName name;
    private String value;
    
    public AttributeReader(QName name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public boolean hasMoreAttributeReaders()
    {
        return false;
    }

    public MessageReader getNextAttributeReader()
    {
        throw new IllegalStateException();
    }

    public boolean hasMoreElementReaders()
    {
        return false;
    }

    public MessageReader getNextElementReader()
    {
        throw new IllegalStateException();
    }

    public QName getName()
    {
        return name;
    }

    public String getLocalName()
    {
        return name.getLocalPart();
    }

    public String getNamespace()
    {
        return name.getNamespaceURI();
    }
}
