package org.codehaus.xfire.service.binding;

import java.util.Collection;

import org.codehaus.xfire.service.Service;

/**
 * A service which can map to an underlying Methods and Classes. Although, it
 * doesn't necessarily need to map to one single object.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface ObjectService
    extends Service
{
    public final static int SCOPE_APPLICATION = 1;

    public final static int SCOPE_SESSION = 2;

    public final static int SCOPE_REQUEST = 3;
    
    public static final String ALLOWED_METHODS = "allowedMethods";

    public static final String SERVICE_IMPL_CLASS = "xfire.serviceImplClass";

    /**
     * Get an operation.
     * @param name The name of the operation.
     * @return
     */
    Operation getOperation(String name);

    /**
     * Get all the operations for this service.
     * @return
     */
    Collection getOperations();

    /**
     * The class which the operations map to.
     * @return
     */
    Class getServiceClass();

    Invoker getInvoker();

    void setInvoker(Invoker invoker);

    int getScope();
    
    BindingProvider getBindingProvider();
}