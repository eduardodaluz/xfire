package org.codehaus.xfire.aegis.yom;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractMessageWriter;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.yom.Attribute;

public class AttributeWriter
    extends AbstractMessageWriter
{
    private Attribute att;
    private String namespace;
    private String name;
    private String prefix;
    
    public AttributeWriter(Attribute att)
    {
        this.att = att;
    }
    
    public void writeValue(Object value)
    {
        att.setValue(value.toString());
    }

    public MessageWriter getAttributeWriter(String name)
    {
        throw new IllegalStateException();
    }

    public MessageWriter getAttributeWriter(String name, String namespace)
    {
        throw new IllegalStateException();
    }

    public MessageWriter getAttributeWriter(QName qname)
    {
        throw new IllegalStateException();
    }

    public MessageWriter getElementWriter(String name)
    {
        throw new IllegalStateException();
    }

    public MessageWriter getElementWriter(String name, String namespace)
    {
        throw new IllegalStateException();
    }

    public MessageWriter getElementWriter(QName qname)
    {
        throw new IllegalStateException();
    }

    public void close()
    {
    }
}
