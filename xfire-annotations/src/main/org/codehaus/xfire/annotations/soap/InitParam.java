package org.codehaus.xfire.annotations.soap;

/**
 * Represents a common implementation of the {@link SOAPMessageHandler} initialization parameter.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class InitParam
{
    private String name;
    private String value;

    /**
     * Initializes a new instance of the <code>InitParam</code> class.
     *
     * @param name  the name
     * @param value the value
     */
    public InitParam(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the initialization parameter.
     *
     * @return the parameter name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the initialization parameter.
     *
     * @param name the parameter name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the value of the initialization parameter.
     *
     * @return the parameter value.
     */

    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the initialization parameter.
     *
     * @param value the parameter name.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

}
