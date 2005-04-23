package org.codehaus.xfire.service;

/**
 * Represents the description of a service operation message part.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessagePartInfo
        implements Visitable
{
    private String name;
    private String namespace;

    /**
     * Initializes a new <code>MessagePartInfo</code> with the given name.
     *
     * @param name
     */
    public MessagePartInfo(String name)
    {
        this.name = name;
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
        this.name = name;
    }

    /**
     * Returns the namespace of this message part info.
     *
     * @return the namespace.
     */
    public String getNamespace()
    {
        return namespace;
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
     * Acceps the given visitor.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
