package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

import org.codehaus.xfire.wsdl.SchemaType;


/**
 * Represents the description of a service operation message header.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessageHeader}.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MessageHeaderInfo
        implements Visitable
{
    private QName name;
    private Class typeClass;
    private MessagePartContainer container;
    private SchemaType schemaType;
    private int index;
    
    MessageHeaderInfo(QName name, Class typeClass, MessagePartContainer container)
    {
        this.name = name;
        this.typeClass = typeClass;
        this.container = container;
    }

    /**
     * @return Returns the name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(QName name)
    {
        this.name = name;
    }

    public Class getTypeClass()
    {
        return typeClass;
    }

    public void setTypeClass(Class typeClass)
    {
        this.typeClass = typeClass;
    }

    public MessagePartContainer getContainer()
    {
        return container;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public SchemaType getSchemaType()
    {
        return schemaType;
    }

    public void setSchemaType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
    }

    /**
     * Acceps the given visitor.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startMessageHeader(this);
        visitor.endMessageHeader(this);
    }
}