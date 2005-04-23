package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the description of a service operation. An operation has a name, and consists of a number of in and out
 * parameters.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class OperationInfo
        implements Visitable
{
    private String name;
    private boolean oneWay;
    private MessageInfo inputMessage;
    private MessageInfo outputMessage;
    private Map faults = new HashMap();

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
    public MessageInfo getInputMessage()
    {
        return inputMessage;
    }

    /**
     * Sets the input message info.
     *
     * @param inputMessage the input message info.
     */
    public void setInputMessage(MessageInfo inputMessage)
    {
        this.inputMessage = inputMessage;
    }

    /**
     * Returns the output message info.
     *
     * @return the output message info.
     */
    public MessageInfo getOutputMessage()
    {
        return outputMessage;
    }

    /**
     * Sets the output message info.
     *
     * @param outputMessage the output message info.
     */
    public void setOutputMessage(MessageInfo outputMessage)
    {
        this.outputMessage = outputMessage;
    }

    /**
     * Adds an fault to this operation.
     *
     * @param faultInfo the fault.
     */
    public void addFault(FaultInfo faultInfo)
    {
        faults.put(faultInfo.getName(), faultInfo);
    }

    /**
     * Returns the fault with the given name, if found.
     *
     * @param name the name.
     * @return the fault; or <code>null</code> if not found.
     */
    public FaultInfo getFault(String name)
    {
        return (FaultInfo) faults.get(name);
    }

    /**
     * Returns all faults for this operation.
     *
     * @return all faults.
     */
    public Collection getFaults()
    {
        return Collections.unmodifiableCollection(faults.values());
    }

    /**
     * Acceps the given visitor. Iterates over the input and output messages, if set.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        if (inputMessage != null)
        {
            inputMessage.accept(visitor);
        }
        if (outputMessage != null)
        {
            outputMessage.accept(visitor);
        }
        for (Iterator iterator = faults.values().iterator(); iterator.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) iterator.next();
            faultInfo.accept(visitor);
        }
    }
}
