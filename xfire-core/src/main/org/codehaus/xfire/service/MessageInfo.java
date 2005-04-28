package org.codehaus.xfire.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;


/**
 * Represents the description of a service operation message.
 * <p/>
 * Messages are created using the {@link OperationInfo#createMessage} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessageInfo
        implements Visitable
{
    protected String name;
    private Map messageParts = new HashMap();
    private List messagePartList = new LinkedList();
    protected OperationInfo operation;

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
     * @param name  the qualified name of the message part.
     * @param clazz the type of the message part.
     */
    public MessagePartInfo addMessagePart(QName name, Class clazz)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }

        if (messageParts.containsKey(name))
        {
            throw new IllegalArgumentException("An message part with name [" + name + "] already exists in this message");
        }

        MessagePartInfo part = new MessagePartInfo(name, clazz, this);
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
        messagePartList.add(part);
    }

    /**
     * Removes an message part from this message.
     *
     * @param name the message part name.
     */
    public void removeMessagePart(QName name)
    {
        MessagePartInfo messagePart = getMessagePart(name);
        if (messagePart != null)
        {
            messageParts.remove(name);
            messagePartList.remove(messagePart);
        }
    }


    /**
     * Returns the message part with the given name, if found.
     *
     * @param name the name.
     * @return the message part; or <code>null</code> if not found.
     */
    public MessagePartInfo getMessagePart(QName name)
    {
        return (MessagePartInfo) messageParts.get(name);
    }

    /**
     * Returns all message parts for this message.
     *
     * @return all message parts.
     */
    public List getMessageParts()
    {
        return Collections.unmodifiableList(messagePartList);
    }

    /**
     * Acceps the given visitor. Iterates over all message part infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Iterator iterator = getMessageParts().iterator(); iterator.hasNext();)
        {
            MessagePartInfo messagePartInfo = (MessagePartInfo) iterator.next();
            messagePartInfo.accept(visitor);
        }
    }
}
