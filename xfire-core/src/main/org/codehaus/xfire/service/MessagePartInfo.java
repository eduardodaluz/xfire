package org.codehaus.xfire.service;

/**
 * Represents the description of a service operation message part.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessagePart(String)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessagePartInfo
        implements Visitable
{
    private String name;
    private String namespace;
    private MessageInfo message;

    /**
     * Initializes a new <code>MessagePartInfo</code> with the given name and message.
     *
     * @param name
     */
    MessagePartInfo(String name, MessageInfo message)
    {
        this.name = name;
        this.message = message;
    }

    /**
     * Returns the name of this message part info.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this message part info.
     *
     * @param name the name.
     */
    public void setName(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        message.removeMessagePart(this.name);
        this.name = name;
        message.addMessagePart(this);
    }

    /**
     * Returns the namespace of this message part info. Defaults to the namespace of the mesage.
     *
     * @return the namespace.
     */
    public String getNamespace()
    {
        return (namespace != null) ? namespace : message.getNamespace();
    }

    /**
     * Sets the namespace of this message part info.
     *
     * @param namespace the namespace.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * Returns the message of this part.
     *
     * @return the message.
     */
    public MessageInfo getMessage()
    {
        return message;
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
