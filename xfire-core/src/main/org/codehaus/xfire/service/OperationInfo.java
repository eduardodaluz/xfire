package org.codehaus.xfire.service;

/**
 * Represents the description of a service operation. An operation has a name, and consists of a number of in and out
 * parameters.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class OperationInfo
{
    private String name;
    private boolean oneWay;
    private MessageInfo inputMessageInfo;
    private MessageInfo outMessageInfo;


    /**
     * Initializes a new instance of the <code>OperationInfo</code> class with the given name.
     *
     * @param name the name of the operation.
     */
    public OperationInfo(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the operation.
     *
     * @return the name of the operation.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the operation.
     *
     * @param name the new name of the operation.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Indicates whether the operation is one-way or not.
     *
     * @return <code>true</code> if the operation is one-way; <code>false</code> otherwise.
     */
    public boolean isOneWay()
    {
        return oneWay;
    }

    /**
     * Determines whether the operation is one-way or not.
     *
     * @param oneWay <code>true</code> if the operation is one-way; <code>false</code> otherwise.
     */
    public void setOneWay(boolean oneWay)
    {
        this.oneWay = oneWay;
    }

    /**
     * Returns the input message info.
     *
     * @return the input message info.
     */
    public MessageInfo getInputMessageInfo()
    {
        return inputMessageInfo;
    }

    /**
     * Sets the input message info.
     *
     * @param inputMessageInfo the input message info.
     */
    public void setInputMessageInfo(MessageInfo inputMessageInfo)
    {
        this.inputMessageInfo = inputMessageInfo;
    }

    /**
     * Returns the output message info.
     *
     * @return the output message info.
     */
    public MessageInfo getOutMessageInfo()
    {
        return outMessageInfo;
    }

    /**
     * Sets the output message info.
     *
     * @param outMessageInfo the output message info.
     */
    public void setOutMessageInfo(MessageInfo outMessageInfo)
    {
        this.outMessageInfo = outMessageInfo;
    }
}
