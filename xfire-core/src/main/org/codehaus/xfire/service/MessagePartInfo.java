package org.codehaus.xfire.service;

import javax.xml.namespace.QName;


/**
 * Represents the description of a service operation message part.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessagePart(String)} method.
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
    private MessageInfo messageInfo;
    
    MessagePartInfo()
    {
    }
    
    MessagePartInfo(QName name, Class typeClass, MessageInfo messageInfo)
    {
        this.name = name;
        this.typeClass = typeClass;
        this.messageInfo = messageInfo;
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
        visitor.visit(this);
    }
}