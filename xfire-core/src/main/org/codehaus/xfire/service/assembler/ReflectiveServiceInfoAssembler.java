package org.codehaus.xfire.service.assembler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;

/**
 * Extends the {@link AbstractServiceInfoAssembler} to add a basic reflection algorithm for building the {@link
 * org.codehaus.xfire.service.ServiceInfo}.
 * <p/>
 * This class does contain the logic for creating a basic {@link org.codehaus.xfire.service.ServiceInfo} from
 * reflection, but it allows subclasses to decide as to which methods are to be included as {@link OperationInfo
 * operations} using the {@link #getOperationMethods(Class)} method.
 * <p/>
 * If a method should be included, a default {@link OperationInfo} is created, but subclasses are given the opportunity
 * to override it using the {@link #populateOperationInfo(OperationInfo, Method)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ReflectiveServiceInfoAssembler
        extends AbstractServiceInfoAssembler
{
    private Class serviceClass;

    /**
     * Constant that declares the suffix given to input {@link MessageInfo} objects created by this assembler.
     */
    public static final String INPUT_MESSAGE_SUFFIX = "Request";
    /**
     * Constant that declares the suffix given to output {@link MessageInfo} objects created by this assembler.
     */
    public static final String OUTPUT_MESSAGE_SUFFIX = "Response";

    /**
     * Initializes a new instance of the <code>ReflectiveServiceInfoAssembler</code> that reflects on the given service
     * class.
     *
     * @param serviceClass
     */
    public ReflectiveServiceInfoAssembler(Class serviceClass)
    {
        this.serviceClass = serviceClass;
    }

    /**
     * Calls the {@link #populateServiceInfo(org.codehaus.xfire.service.ServiceInfo, Class)} method and then iterates
     * through all methods of the service class, calling {@link #populateOperationInfo(org.codehaus.xfire.service.OperationInfo,
            * java.lang.reflect.Method)} for each of them.
     *
     * @param serviceInfo the service info.
     */
    protected final void populate(ServiceInfo serviceInfo)
    {
        serviceInfo.setName(ServiceUtils.makeServiceNameFromClassName(serviceClass));
        serviceInfo.setNamespace(NamespaceHelper.makeNamespaceFromClassName(serviceClass.getName(), "http"));
        populateServiceInfo(serviceInfo, serviceClass);

        final Method[] methods = getOperationMethods(serviceClass);
        for (int i = 0; i < methods.length; i++)
        {
            OperationInfo operationInfo = getOperationInfo(methods[i], serviceInfo);
            populateOperationInfo(operationInfo, methods[i]);
        }
    }

    /**
     * Creates a basic operation info from the given method.
     *
     * @param method      the method.
     * @param serviceInfo the service info.
     * @return the created operation info.
     */
    private OperationInfo getOperationInfo(Method method, ServiceInfo serviceInfo)
    {
        OperationInfo operationInfo = new OperationInfo(method.getName());
        final boolean hasParameters = method.getParameterTypes().length != 0;
        final boolean hasException = method.getExceptionTypes().length != 0;
        final boolean returnsVoid = method.getReturnType().isAssignableFrom(Void.TYPE);
        operationInfo.setOneWay(returnsVoid && !hasParameters && !hasException);
        if (hasParameters)
        {
            MessageInfo inputMessage = new MessageInfo(operationInfo.getName() + INPUT_MESSAGE_SUFFIX);
            inputMessage.setNamespace(serviceInfo.getNamespace());
            operationInfo.setInputMessageInfo(inputMessage);
            for (int i = 0; i < method.getParameterTypes().length; i++)
            {
                MessagePartInfo partInfo = new MessagePartInfo(operationInfo.getName() + "in" + i);
                partInfo.setNamespace(serviceInfo.getNamespace());
                populateInputMessagePartInfo(partInfo, method, i);
                inputMessage.addMethodPartInfo(partInfo);
            }
        }
        if (!returnsVoid)
        {
            MessageInfo outputMessage = new MessageInfo(operationInfo.getName() + OUTPUT_MESSAGE_SUFFIX);
            outputMessage.setNamespace(serviceInfo.getNamespace());
            operationInfo.setOutputMessageInfo(outputMessage);
            MessagePartInfo partInfo = new MessagePartInfo(operationInfo.getName() + "out");
            partInfo.setNamespace(serviceInfo.getNamespace());
            populateOutputMessagePartInfo(partInfo, method);
            outputMessage.addMethodPartInfo(partInfo);

        }
        if (hasException)
        {
            Class[] exceptionTypes = method.getExceptionTypes();
            for (int i = 0; i < exceptionTypes.length; i++)
            {
                FaultInfo faultInfo = new FaultInfo(exceptionTypes[i].getName());
                faultInfo.setNamespace(serviceInfo.getNamespace());
                operationInfo.addFaultInfo(faultInfo);
            }
        }

        return operationInfo;
    }

    /**
     * Gets the methods that should be exposed as operations. Default implementation returns the public, non-static
     * declared methods of the class.
     *
     * @param serviceClass the service class.
     */
    protected Method[] getOperationMethods(Class serviceClass)
    {
        List methods = new ArrayList(Arrays.asList(serviceClass.getDeclaredMethods()));
        for (Iterator iterator = methods.iterator(); iterator.hasNext();)
        {
            Method method = (Method) iterator.next();
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()))
            {
                iterator.remove();
            }
        }
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    /**
     * Allows subclasses to customize the given service descriptor with the information from the class.
     *
     * @param serviceInfo  the service descriptor.
     * @param serviceClass the service class.
     */
    protected void populateServiceInfo(ServiceInfo serviceInfo, final Class serviceClass)
    {
    }

    /**
     * Allows subclasses to customize the given operation descriptor with the information from the method.
     *
     * @param operationInfo the operation descriptor.
     * @param method        the method.
     */
    protected void populateOperationInfo(OperationInfo operationInfo, final Method method)
    {
    }

    /**
     * Allows subclasses to customize the given output message part with the information from the method.
     *
     * @param partInfo the part info.
     * @param method   the method.
     */
    protected void populateOutputMessagePartInfo(MessagePartInfo partInfo, Method method)
    {
    }

    /**
     * Allows subclasses to customize the given input message part with the information from the method.
     *
     * @param partInfo  the part info.
     * @param method    the method.
     * @param parameter the index of the parameter to base the part on.
     */
    protected void populateInputMessagePartInfo(MessagePartInfo partInfo, Method method, int parameter)
    {
    }

}
