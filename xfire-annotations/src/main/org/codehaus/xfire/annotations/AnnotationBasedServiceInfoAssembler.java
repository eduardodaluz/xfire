package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.assembler.ReflectiveServiceInfoAssembler;

/**
 * @author Arjen Poutsma
 */
public class AnnotationBasedServiceInfoAssembler
        extends ReflectiveServiceInfoAssembler
{
    private WebAnnotations webAnnotations;

    /**
     * Initializes a new instance of the <code>AnnotationBasedServiceInfoAssembler</code> that uses the given web
     * annotations to reflect on the given service class.
     *
     * @param serviceClass   the service class.
     * @param webAnnotations the web annotations.
     */
    public AnnotationBasedServiceInfoAssembler(Class serviceClass, WebAnnotations webAnnotations)
    {
        super(serviceClass);
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
    protected void populateServiceInfo(ServiceInfo service, final Class serviceClass)
    {
        if (!webAnnotations.hasWebServiceAnnotation(serviceClass))
        {
            throw new AnnotationException("Class " + serviceClass.getName() + " does not have a WebService annotation");
        }
        WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(serviceClass);
        webServiceAnnotation.populate(service);
    }

    /**
     * Populates the given {@link OperationInfo} with the annotations found on the method. Both the {@link
     * WebMethodAnnotation web method annotation} and the {@link WebAnnotations#hasOnewayAnnotation(Method) one way
     * annotation} are used for populating the operation info.
     *
     * @param operationInfo the operation info.
     * @param method        the method.
     */
    protected void populateOperationInfo(OperationInfo operationInfo, final Method method)
    {
        // we don't throw an exception here, since the given method might be part of an endpoint interface
        if (webAnnotations.hasWebMethodAnnotation(method))
        {
            WebMethodAnnotation webMethodAnnotation = webAnnotations.getWebMethodAnnotation(method);
            webMethodAnnotation.populate(operationInfo);
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

    /**
     * Returns the input message info for the given method. The returned message contains parts for all parameters that
     * have not been annotated with the {@link WebParamAnnotation#MODE_OUT} mode. If this results in no parts, this
     * method returns <code>null</code>.
     *
     * @param method    the method.
     * @param namespace the default namespace.
     * @return the output message info for the method; or <code>null</code> if the method has no parameters.
     */
    protected MessageInfo getInputMessageInfo(Method method, String namespace)
    {
        MessageInfo inputMessage = new MessageInfo(method.getName() + INPUT_MESSAGE_SUFFIX);
        inputMessage.setNamespace(namespace);
        final Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            MessagePartInfo partInfo = new MessagePartInfo(
                    inputMessage.getName() + INPUT_MESSAGE_PART_INFIX + inputMessage.getMessageParts().size());
            partInfo.setNamespace(namespace);

            if (webAnnotations.hasWebParamAnnotation(method, i))
            {
                WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, i);
                if ((webParamAnnotation.getMode() == WebParamAnnotation.MODE_OUT) ||
                        (webParamAnnotation.isHeader()))
                {
                    continue;
                }
                else
                {
                    webParamAnnotation.populate(partInfo);
                }
            }
            inputMessage.addMethodPart(partInfo);
        }
        if (!inputMessage.getMessageParts().isEmpty())
        {
            return inputMessage;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the output message info for the given method. The returned message contains parts for all parameters that
     * have been annotated with a {@link WebParamAnnotation#MODE_INOUT} or {@link WebParamAnnotation#MODE_OUT} mode. The
     * message contains an additional part info if the method returns a value. If the method has no outwards parameters
     * and returns <code>void</code>; this methods returns <code>null</code>.
     *
     * @param method    the method.
     * @param namespace the default namespace.
     * @return the output message info for the method; or <code>null</code> if the method returns <code>void</code>.
     */
    protected MessageInfo getOutputMessageInfo(Method method, String namespace)
    {
        MessageInfo outputMessage = new MessageInfo(method.getName() + OUTPUT_MESSAGE_SUFFIX);
        outputMessage.setNamespace(namespace);
        if (!method.getReturnType().isAssignableFrom(Void.TYPE))
        {
            MessagePartInfo partInfo = new MessagePartInfo(outputMessage.getName() + OUTPUT_MESSAGE_PART_INFIX +
                                                           outputMessage.getMessageParts().size());
            partInfo.setNamespace(namespace);
            if (webAnnotations.hasWebResultAnnotation(method))
            {
                WebResultAnnotation webResultAnnotation = webAnnotations.getWebResultAnnotation(method);
                webResultAnnotation.populate(partInfo);
            }
            outputMessage.addMethodPart(partInfo);
        }
        final Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (webAnnotations.hasWebParamAnnotation(method, i))
            {
                WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, i);
                if ((webParamAnnotation.getMode() == WebParamAnnotation.MODE_OUT) ||
                        (webParamAnnotation.getMode() == WebParamAnnotation.MODE_INOUT))
                {
                    MessagePartInfo partInfo = new MessagePartInfo(outputMessage.getName() + OUTPUT_MESSAGE_PART_INFIX +
                                                                   outputMessage.getMessageParts().size());
                    partInfo.setNamespace(namespace);
                    webParamAnnotation.populate(partInfo);
                    outputMessage.addMethodPart(partInfo);
                }

            }
        }
        if (!outputMessage.getMessageParts().isEmpty())
        {
            return outputMessage;
        }
        else
        {
            return null;
        }
    }
}
