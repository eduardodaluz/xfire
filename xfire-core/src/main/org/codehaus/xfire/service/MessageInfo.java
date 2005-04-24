package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the description of a service operation message.
 * <p/>
 * Messages are created using the {@link OperationInfo#createMessage(String)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessageInfo
        implements Visitable
{
    protected String name;
    protected String namespace;
    private Map messageParts = new HashMap();
    protected OperationInfo operation;

    private MessageInfo(String name)
    {
        this.name = name;
    }

    /**
     * Initializes a new instance of the <code>MessageInfo</code> class with the given name and operation.
     *
     * @param name      the name.
     * @param operation the operation.
     */
    MessageInfo(String name, OperationInfo operation)
    {
        this.name = name;
        this.operation = operation;
    }

    /**
     * Returns the name of the message info.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the message info.
     *
     * @param name the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the namespace of the message. Defaults to the namespace of the service.
     *
     * @return the namespace of the message.
     */
    public String getNamespace()
    {
        return (namespace != null) ? namespace : operation.getService().getNamespace();
    }

    /**
     * Sets the namespace of the message.
     *
     * @param namespace the namespace of the message.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * Returns the operation of this message.
     *
     * @return the operation.
     */
    public OperationInfo getOperation()
    {
        return operation;
    }

    /**
     * Adds an message part to this message info.
     *
     * @param name the message part name.
     */
    public MessagePartInfo addMessagePart(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        if (messageParts.containsKey(name))
        {
            throw new IllegalArgumentException(
                    "An message part with name [" + name + "] already exists in this message");
        }
        MessagePartInfo part = new MessagePartInfo(name, this);
        addMessagePart(part);
        return part;
    }

    /**
     * Adds an message part to this message.
     *
     * @param part the message part.
     */
    void addMessagePart(MessagePartInfo part)
    {
        messageParts.put(part.getName(), part);
    }

    /**
     * Removes an message part from this message.
     *
     * @param name the message part name.
     */
    public void removeMessagePart(String name)
    {
        messageParts.remove(name);
    }


    /**
     * Returns the message part with the given name, if found.
     *
     * @param name the name.
     * @return the message part; or <code>null</code> if not found.
     */
    public MessagePartInfo getMessagePart(String name)
    {
        return (MessagePartInfo) messageParts.get(name);
    }

    /**
     * Returns all message parts for this message.
     *
     * @return all message parts.
     */
    public Collection getMessageParts()
    {
        return Collections.unmodifiableCollection(messageParts.values());
    }

    /**
     * Acceps the given visitor. Iterates over all message part infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Iterator iterator = messageParts.values().iterator(); iterator.hasNext();)
        {
            MessagePartInfo messagePartInfo = (MessagePartInfo) iterator.next();
            messagePartInfo.accept(visitor);
        }
    }
}
