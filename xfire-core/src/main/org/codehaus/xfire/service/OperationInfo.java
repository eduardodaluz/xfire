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
    private MessageInfo inputMessageInfo;
    private MessageInfo outputMessageInfo;
    private Map faultInfos = new HashMap();

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
    public MessageInfo getOutputMessageInfo()
    {
        return outputMessageInfo;
    }

    /**
     * Sets the output message info.
     *
     * @param outputMessageInfo the output message info.
     */
    public void setOutputMessageInfo(MessageInfo outputMessageInfo)
    {
        this.outputMessageInfo = outputMessageInfo;
    }

    /**
     * Adds an fault info to this operation info.
     *
     * @param faultInfo the fault info.
     */
    public void addFaultInfo(FaultInfo faultInfo)
    {
        faultInfos.put(faultInfo.getName(), faultInfo);
    }

    /**
     * Returns the fault info with the given name, if found.
     *
     * @param name the name.
     * @return the fault info; or <code>null</code> if not found.
     */
    public FaultInfo getFaultInfo(String name)
    {
        return (FaultInfo) faultInfos.get(name);
    }

    /**
     * Returns all fault infos for this operation.
     *
     * @return all fault infos.
     */
    public Collection getFaultInfos()
    {
        return Collections.unmodifiableCollection(faultInfos.values());
    }

    /**
     * Acceps the given visitor. Iterates over the input and output messages, if set.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        if (inputMessageInfo != null)
        {
            inputMessageInfo.accept(visitor);
        }
        if (outputMessageInfo != null)
        {
            outputMessageInfo.accept(visitor);
        }
        for (Iterator iterator = faultInfos.values().iterator(); iterator.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) iterator.next();
            faultInfo.accept(visitor);
        }
    }
}
