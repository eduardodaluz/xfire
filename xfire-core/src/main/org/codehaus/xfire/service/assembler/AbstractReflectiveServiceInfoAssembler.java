package org.codehaus.xfire.service.assembler;

import java.lang.reflect.Method;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

/**
 * Extends the {@link AbstractServiceInfoAssembler} to add a basic reflection algorithm for building the {@link
 * org.codehaus.xfire.service.ServiceInfo}. Before any operations can be performed,
 * <p/>
 * This class does contain the logic for creating {@link org.codehaus.xfire.service.ServiceInfo} from reflection, but it
 * makes no decisions as to which methods are to be included as {@link OperationInfo operations}. Instead, it gives
 * subclasses a chance to select exposable methods through the {@link #include(java.lang.reflect.Method)} template
 * method.
 * <p/>
 * If a method should be included, subclasses are given the opportunity to populate operations using the {@link
 * #populateOperation(org.codehaus.xfire.service.OperationInfo, java.lang.reflect.Method)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public abstract class AbstractReflectiveServiceInfoAssembler
        extends AbstractServiceInfoAssembler
{
    private Class serviceClass;

    /**
     * Calls the {@link #populate(org.codehaus.xfire.service.ServiceInfo, Class)} method and then iterates through all
     * methods of the service class, calling {@link #populate(org.codehaus.xfire.service.OperationInfo,
            * java.lang.reflect.Method, Class)} for each of them. Iterates through all the
     *
     * @param serviceInfo the service info.
     */
    protected void populate(ServiceInfo serviceInfo)
    {
        if (serviceClass == null)
        {
            throw new XFireRuntimeException("No service class set");
        }
        populate(serviceInfo, serviceClass);

        final Method[] methods = getOperationMethods(serviceClass);
        for (int i = 0; i < methods.length; i++)
        {

            OperationInfo operationInfo = new OperationInfo(methods[i].getName());
            populate(operationInfo, methods[i]);
        }
    }


    /**
     * Sets the service class to reflect upon. This class <strong>must</strong> be set before {@link #getServiceInfo()}
     * can be called.
     *
     * @param serviceClass the service class.
     */
    public void setServiceClass(Class serviceClass)
    {
        this.serviceClass = serviceClass;
    }

    /**
     * Gets the methods that should be exposed as operations. Default implementation returns the declared methods of the
     * class.
     *
     * @param serviceClass the service class.
     */
    protected Method[] getOperationMethods(Class serviceClass)
    {
        return serviceClass.getDeclaredMethods();
    }


    /**
     * Template method that should populate the given service info with the information from the class.
     *
     * @param serviceInfo  the service descriptor.
     * @param serviceClass the service class.
     */
    protected abstract void populate(ServiceInfo serviceInfo, final Class serviceClass);

    /**
     * Template method that should populate the given operation descriptor with the information from the method.
     *
     * @param operationInfo the operation descriptor.
     * @param method        the method.
     */
    protected abstract void populate(OperationInfo operationInfo, final Method method);
}
