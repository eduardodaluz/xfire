package org.codehaus.xfire.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.AbstractContext;

/**
 * Represents an description of a service. A service consists of a number of <code>OperationInfo</code> objects, a
 * qualified name, and a service class.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see OperationInfo
 */
public class ServiceInfo
    extends AbstractContext
    implements Visitable
{
    private QName name;
    private Map operations = new HashMap();
    private Map methodToOp = new HashMap();
    private Class serviceClass;
    private QName portType;
    private boolean wrapped;
    
    /**
     * Initializes a new instance of the <code>ServiceInfo</code> class with the given qualified name and service
     * class.
     *
     * @param name         the qualified name.
     * @param serviceClass the service class.
     */
    public ServiceInfo(QName name, QName portType, Class serviceClass)
    {
        this.name = name;
        this.portType = portType;
        this.serviceClass = serviceClass;
    }

    /**
     * Acceps the given visitor. Iterates over all operation infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startService(this);
        for (Iterator iterator = operations.values().iterator(); iterator.hasNext();)
        {
            OperationInfo operationInfo = (OperationInfo) iterator.next();
            operationInfo.accept(visitor);
        }
        visitor.endService(this);
    }

    /**
     * Adds an operation to this service.
     *
     * @param name the qualified name of the operation.
     * @return the operation.
     */
    public OperationInfo addOperation(String name, Method method)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        if (operations.containsKey(name))
        {
            throw new IllegalArgumentException("An operation with name [" + name + "] already exists in this service");
        }

        OperationInfo operation = new OperationInfo(name, method, this);
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
        methodToOp.put(operation.getMethod(), operation);
    }

    /**
     * Returns the qualified name of the service descriptor.
     *
     * @return the qualified name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * Sets the qualified name of the service descriptor.
     *
     * @param name the new qualified name.
     */
    public void setName(QName name)
    {
        this.name = name;
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

    public OperationInfo getOperation(Method m)
    {
        return (OperationInfo) methodToOp.get(m);
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
     * Returns the service class of the service descriptor.
     *
     * @return
     */
    public Class getServiceClass()
    {
        return serviceClass;
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

    public QName getPortType()
    {
        return portType;
    }

    public void setPortType(QName portType)
    {
        this.portType = portType;
    }

    public boolean isWrapped()
    {
        return wrapped;
    }

    public void setWrapped(boolean wrapped)
    {
        this.wrapped = wrapped;
    }
}