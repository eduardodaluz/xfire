package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents an description of a service. A service consists of a number of {@link OperationInfo operations}, a name
 * and a namespace.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ServiceInfo
        implements Visitable
{
    private String name;
    private String namespace;
    private Map operations = new HashMap();

    /**
     * Returns the name of the service.
     *
     * @return the name of the service.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the service.
     *
     * @param name the new name of the service.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the namespace of the service.
     *
     * @return the namespace of the service.
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Sets the namespace of the service.
     *
     * @param namespace the namespace of the service.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * Adds an operation to this service.
     *
     * @param name the name of the operation.
     * @return the operation.
     */
    public OperationInfo addOperation(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        if (operations.containsKey(name))
        {
            throw new IllegalArgumentException("An operation with name [" + name + "] already exists in this service");
        }
        OperationInfo operation = new OperationInfo(name, this);
        addOperation(operation);
        return operation;
    }

    /**
     * Adds an operation to this service.
     *
     * @param operation the operation.
     */
    void addOperation(OperationInfo operation)
    {
        operations.put(operation.getName(), operation);
    }

    /**
     * Removes an operation from this service.
     *
     * @param name the operation name.
     */
    public void removeOperation(String name)
    {
        operations.remove(name);
    }

    /**
     * Returns the operation info with the given name, if found.
     *
     * @param name the name.
     * @return the operation; or <code>null</code> if not found.
     */
    public OperationInfo getOperation(String name)
    {
        return (OperationInfo) operations.get(name);
    }

    /**
     * Returns all operations for this service.
     *
     * @return all operations.
     */
    public Collection getOperations()
    {
        return Collections.unmodifiableCollection(operations.values());
    }

    /**
     * Acceps the given visitor. Iterates over all operation infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Iterator iterator = operations.values().iterator(); iterator.hasNext();)
        {
            OperationInfo operationInfo = (OperationInfo) iterator.next();
            operationInfo.accept(visitor);
        }
    }
}
