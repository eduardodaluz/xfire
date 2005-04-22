package org.codehaus.xfire.service.assembler;

import java.lang.reflect.Method;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;

/**
 * Simple extension of the {@link AbstractReflectiveServiceInfoAssembler} that exposes all methods as operations. The
 * short service class name is used as service name, and all methods are exposed as operations.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class SimpleReflectiveServiceInfoAssembler
        extends AbstractReflectiveServiceInfoAssembler
{
    /**
     * Populates the given {@link ServiceInfo} with the class. The short class name (i.e. without the package) is used
     * as service name. The reserved package name is used as namespace.
     *
     * @param service      the service descriptor.
     * @param serviceClass the service class.
     */
    protected void populate(ServiceInfo service, Class serviceClass)
    {
        service.setName(ServiceUtils.makeServiceNameFromClassName(serviceClass));
        service.setNamespace(NamespaceHelper.makeNamespaceFromClassName(serviceClass.getName(), "http"));
    }

    /**
     * Populates the given {@link OperationInfo} with the method. The method name is used as operation name. If the
     * method returns <code>void</code>, has no parameters, and has no declared exceptions, it is marked as {@link
     * OperationInfo#isOneWay()  one way}.
     *
     * @param operation the operation descriptor.
     * @param method    the method.
     */
    protected void populate(OperationInfo operation, final Method method)
    {
        operation.setOneWay((method.getReturnType().isAssignableFrom(Void.TYPE)) &&
                            (method.getParameterTypes().length == 0) &&
                            (method.getExceptionTypes().length == 0));
    }
}
