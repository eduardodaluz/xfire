package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

import org.codehaus.xfire.wsdl.SchemaType;


/**
 * Represents the description of a service operation message part.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessagePart} or {@link FaultInfo#addMessagePart}  method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public class MessagePartInfo
        implements Visitable
{
    private QName name;
    private Class typeClass;
    private MessagePartContainer container;
    private SchemaType schemaType;

    MessagePartInfo(QName name, Class typeClass, MessagePartContainer container)
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
        visitor.startMessagePart(this);
        visitor.endMessagePart(this);
    }
}