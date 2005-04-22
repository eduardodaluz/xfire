package org.codehaus.xfire.service;

/**
 * Represents the description of a service operation message.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessageInfo
{
    private String name;

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
}
