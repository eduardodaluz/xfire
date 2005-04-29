package org.codehaus.xfire.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * An operation that be performed on a service.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 20, 2004
 */
public class OperationInfo
        implements Visitable
{
    private String name;
    // TODO: change this to a ServiceInfo
    private Service service;
    private boolean oneWay;
    private MessageInfo inputMessage;
    private MessageInfo outputMessage;
    // maps string names to FaultInfo objects
    private Map faults = new HashMap();
    private Method method;

    /**
     * Initializes a new instance of the <code>OperationInfo</code> class with the given name and service.
     *
     * @param name    the name of the operation.
     * @param service the service.
     */
    // TODO: make this constructor package local, and change the Service to a ServiceInfo
    public OperationInfo(String name, Method method, Service service)
    {
        this.name = name;
        this.service = service;
        this.method = method;
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
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }

        service.removeOperation(this.name);
        this.name = name;
        service.addOperation(this);
    }

    public Method getMethod()
    {
        return method;
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
     * Returns the service descriptor of this operation.
     *
     * @return the service.
     */
    public Service getService()
    {
        return service;
    }

    /**
     * Creates a new message. This message can be set as either {@link #setInputMessage(MessageInfo) input message} or
     * {@link #setOutputMessage(MessageInfo) output message}.
     *
     * @param name the name of the message.
     * @return the created message.
     */
    public MessageInfo createMessage(QName name)
    {
        MessageInfo message = new MessageInfo(name, this);
        return message;
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
     * @param name the fault name.
     */
    public FaultInfo addFault(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        if (faults.containsKey(name))
        {
            throw new IllegalArgumentException("A fault with name [" + name + "] already exists in this operation");
        }
        FaultInfo fault = new FaultInfo(name, this);
        addFault(fault);
        return fault;
    }

    /**
     * Adds a fault to this operation.
     *
     * @param fault the fault.
     */
    void addFault(FaultInfo fault)
    {
        faults.put(fault.getName(), fault);
    }

    /**
     * Removes a fault from this operation.
     *
     * @param name the qualified fault name.
     */
    public void removeFault(String name)
    {
        faults.remove(name);
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
        visitor.startOperation(this);
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
        visitor.endOperation(this);
    }
}