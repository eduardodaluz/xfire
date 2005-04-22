package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.assembler.AbstractReflectiveServiceInfoAssembler;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;

/**
 * @author Arjen Poutsma
 */
public class AnnotationBasedServiceInfoAssembler
        extends AbstractReflectiveServiceInfoAssembler
{
    private WebAnnotations webAnnotations;

    /**
     * Initializes a new instance of the <code>AnnotationBasedServiceInfoAssembler</code> with the given web
     * annotations.
     *
     * @param webAnnotations the web annotations.
     */
    public AnnotationBasedServiceInfoAssembler(WebAnnotations webAnnotations)
    {
        this.webAnnotations = webAnnotations;
    }

    /**
     * Gets the methods that should be exposed as operations. If the given service class has an {@link
     * WebServiceAnnotation#getEndpointInterface() endpoint interface}, all methods on that interface are mapped to
     * operations. Otherwise, returns all declared methods of the class that bear the {@link WebMethodAnnotation}.
     *
     * @param serviceClass the service class.
     */
    protected Method[] getOperationMethods(Class serviceClass)
    {
        Class endpointInterface = getEndpointInterface(serviceClass);
        if (endpointInterface != null)
        {
            return super.getOperationMethods(endpointInterface);
        }
        else
        {
            ArrayList methods = new ArrayList(Arrays.asList(super.getOperationMethods(serviceClass)));
            for (Iterator iterator = methods.iterator(); iterator.hasNext();)
            {
                Method method = (Method) iterator.next();
                if (!webAnnotations.hasWebMethodAnnotation(method))
                {
                    iterator.remove();
                }
            }
            return (Method[]) methods.toArray(new Method[methods.size()]);
        }
    }

    /**
     * Returns the {@link WebServiceAnnotation#getEndpointInterface() endpoint interface} of the service class, if the
     * given service class has the annotation. Otherwise, returns <code>null</code>.
     *
     * @param serviceClass the service class.
     * @return the endpoint interface, or <code>null</code> if not found.
     */
    private Class getEndpointInterface(Class serviceClass)
    {
        WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(serviceClass);

        if (webServiceAnnotation.getEndpointInterface().length() != 0)
        {
            try
            {
                return getClass().getClassLoader().loadClass(webServiceAnnotation.getEndpointInterface());
            }
            catch (ClassNotFoundException e)
            {
                try
                {
                    return Thread.currentThread().getContextClassLoader().loadClass(
                            webServiceAnnotation.getEndpointInterface());
                }
                catch (ClassNotFoundException e1)
                {
                    throw new AnnotationException("Couldn't find endpoint interface " +
                                                  webServiceAnnotation.getEndpointInterface(), e);
                }
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Populates the given {@link ServiceInfo} with the annotations found on the service class.
     *
     * @param service      the service information.
     * @param serviceClass the service class.
     * @throws AnnotationException when <code>serviceClass</code> does not bear a {@link WebServiceAnnotation}.
     */
    protected void populate(ServiceInfo service, final Class serviceClass)
    {
        if (!webAnnotations.hasWebServiceAnnotation(serviceClass))
        {
            throw new AnnotationException("Class " + serviceClass.getName() + " does not have a WebService annotation");
        }
        WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(serviceClass);
        if (webServiceAnnotation.getName().length() != 0)
        {
            service.setName(webServiceAnnotation.getName());
        }
        else
        {
            service.setName(ServiceUtils.makeServiceNameFromClassName(serviceClass));
        }
        if (webServiceAnnotation.getTargetNamespace().length() != 0)
        {
            service.setNamespace(webServiceAnnotation.getTargetNamespace());
        }
        else
        {
            service.setNamespace(NamespaceHelper.makeNamespaceFromClassName(serviceClass.getName(), "http"));
        }
    }

    /**
     * Populates the given {@link OperationInfo} with the annotations found on the method. Both the {@link
     * WebMethodAnnotation web method annotation} and the {@link WebAnnotations#hasOnewayAnnotation(Method) one way
     * annotation} are used for populating the operation info.
     *
     * @param operationInfo the operation info.
     * @param method        the method.
     */
    protected void populate(OperationInfo operationInfo, final Method method)
    {
        if (webAnnotations.hasWebMethodAnnotation(method))
        {
            WebMethodAnnotation webMethodAnnotation = webAnnotations.getWebMethodAnnotation(method);
            if (webMethodAnnotation.getOperationName().length() > 0)
            {
                operationInfo.setName(webMethodAnnotation.getOperationName());
            }
            if (webAnnotations.hasOnewayAnnotation(method))
            {
                if ((!method.getReturnType().isAssignableFrom(Void.TYPE)) ||
                        (method.getParameterTypes().length != 0) ||
                        (method.getExceptionTypes().length != 0))
                {
                    throw new AnnotationException("Method [" + method.getName() + "] has the OneWay annotation, but " +
                                                  "is not suitable");
                }
                operationInfo.setOneWay(true);
            }
        }
    }

}
