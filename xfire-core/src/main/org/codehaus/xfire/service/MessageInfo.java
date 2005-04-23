package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the description of a service operation message.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessageInfo
        implements Visitable
{
    private String name;
    private String namespace;
    private Map messageParts = new HashMap();

    /**
     * Initializes a new instance of the <code>MessageInfo</code> class with the given name.
     *
     * @param name the name.
     */
    public MessageInfo(String name)
    {
        this.name = name;
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
     * Returns the namespace of the message.
     *
     * @return the namespace of the message.
     */
    public String getNamespace()
    {
        return namespace;
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
     * Adds an message part to this message info.
     *
     * @param messagePartInfo the message part.
     */
    public void addMethodPart(MessagePartInfo messagePartInfo)
    {
        messageParts.put(messagePartInfo.getName(), messagePartInfo);
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
