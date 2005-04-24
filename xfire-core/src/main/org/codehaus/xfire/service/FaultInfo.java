package org.codehaus.xfire.service;

import java.util.Iterator;

/**
 * Represents the description of a service operation fault.
 * <p/>
 * Faults are created using the {@link OperationInfo#addFault(String)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class FaultInfo
        extends MessageInfo
{
    /**
     * Initializes a new instance of the <code>FaultInfo</code> class with the given name and operation
     *
     * @param name the name.
     */
    FaultInfo(String name, OperationInfo operation)
    {
        super(name, operation);
    }

    /**
     * Sets the name of the fault.
     *
     * @param name the name.
     */
    public void setName(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        operation.removeFault(getName());
        this.name = name;
        operation.addFault(this);
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
