package org.codehaus.xfire.service;

import javax.xml.namespace.QName;


/**
 * Represents the description of a service operation message part.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessagePart} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public class MessagePartInfo
        implements Visitable
{
    private QName name;
    private QName schemaType;
    private Class typeClass;
    private MessagePartContainer container;


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

    /**
     * The xml schema type of this parameter.
     *
     * @return
     */
    public QName getSchemaType()
    {
        return schemaType;
    }

    public void setSchemaType(QName schemaType)
    {
        this.schemaType = schemaType;
    }

    public Class getTypeClass()
    {
        return typeClass;
    }

    public void setTypeClass(Class typeClass)
    {
        this.typeClass = typeClass;
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