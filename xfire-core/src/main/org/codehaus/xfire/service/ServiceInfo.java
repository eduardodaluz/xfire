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
    private Map operationInfos = new HashMap();

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
     * Adds an operation info to this service info.
     *
     * @param operationInfo the operation info.
     */
    public void addOperationInfo(OperationInfo operationInfo)
    {
        operationInfos.put(operationInfo.getName(), operationInfo);
    }

    /**
     * Returns the operation info with the given name, if found.
     *
     * @param name the name.
     * @return the operation info; or <code>null</code> if not found.
     */
    public OperationInfo getOperationInfo(String name)
    {
        return (OperationInfo) operationInfos.get(name);
    }

    /**
     * Returns all operation infos for this service.
     *
     * @return all operation infos.
     */
    public Collection getOperationInfos()
    {
        return Collections.unmodifiableCollection(operationInfos.values());
    }

    /**
     * Acceps the given visitor. Iterates over all operation infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Iterator iterator = operationInfos.values().iterator(); iterator.hasNext();)
        {
            OperationInfo operationInfo = (OperationInfo) iterator.next();
            operationInfo.accept(visitor);
        }
    }
}
