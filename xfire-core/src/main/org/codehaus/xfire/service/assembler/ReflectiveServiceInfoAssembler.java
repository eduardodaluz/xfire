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
     * Constant that declares the infix given to input {@link MessagePartInfo} objects created by this assembler.
     */
    public static final String INPUT_MESSAGE_PART_INFIX = "in";
    /**
     * Constant that declares the infix given to output {@link MessagePartInfo} objects created by this assembler.
     */
    public static final String OUTPUT_MESSAGE_PART_INFIX = "out";

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
            OperationInfo operationInfo = getOperationInfo(methods[i], serviceInfo.getNamespace());
            populateOperationInfo(operationInfo, methods[i]);
        }
    }

    /**
     * Creates a basic operation info from the given method.
     *
     * @param method    the method.
     * @param namespace the namespace.
     * @return the created operation info.
     */
    private OperationInfo getOperationInfo(Method method, String namespace)
    {
        OperationInfo operationInfo = new OperationInfo(method.getName());
        operationInfo.setInputMessage(getInputMessageInfo(method, namespace));
        operationInfo.setOutputMessage(getOutputMessageInfo(method, namespace));
        Class[] faultTypes = method.getExceptionTypes();
        operationInfo.setOneWay((operationInfo.getInputMessage() == null) &&
                                (operationInfo.getOutputMessage() == null) &&
                                (faultTypes.length == 0));
        if (faultTypes.length != 0)
        {
            Class[] exceptionTypes = method.getExceptionTypes();
            for (int i = 0; i < exceptionTypes.length; i++)
            {
                FaultInfo faultInfo = new FaultInfo(exceptionTypes[i].getName());
                faultInfo.setNamespace(namespace);
                operationInfo.addFault(faultInfo);
            }
        }

        return operationInfo;
    }

    /**
     * Returns the methods that should be exposed as operations. Default implementation returns the public, non-static
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
     * Returns the input message info for the given method. Default implementation returns a message with a part for
     * each of the method parameters it it has parameters; and <code>null</code> if it has no parameters..
     *
     * @param method    the method.
     * @param namespace the default namespace.
     * @return the output message info for the method; or <code>null</code> if the method has no parameters.
     */
    protected MessageInfo getInputMessageInfo(Method method, String namespace)
    {
        final Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 0)
        {
            MessageInfo inputMessage = new MessageInfo(method.getName() + INPUT_MESSAGE_SUFFIX);
            inputMessage.setNamespace(namespace);
            for (int i = 0; i < parameterTypes.length; i++)
            {
                MessagePartInfo partInfo = new MessagePartInfo(inputMessage.getName() + INPUT_MESSAGE_PART_INFIX + i);
                partInfo.setNamespace(namespace);
                inputMessage.addMethodPart(partInfo);
            }
            return inputMessage;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the output message info for the given method. Default implementation returns a message with a single part
     * info if the method returns a value; and <code>null</code> if it return <code>void</code>.
     *
     * @param method    the method.
     * @param namespace the default namespace.
     * @return the output message info for the method; or <code>null</code> if the method returns <code>void</code>.
     */
    protected MessageInfo getOutputMessageInfo(Method method, String namespace)
    {
        if (!method.getReturnType().isAssignableFrom(Void.TYPE))
        {
            MessageInfo outputMessage = new MessageInfo(method.getName() + OUTPUT_MESSAGE_SUFFIX);
            outputMessage.setNamespace(namespace);
            MessagePartInfo partInfo = new MessagePartInfo(outputMessage.getName() + OUTPUT_MESSAGE_PART_INFIX);
            outputMessage.addMethodPart(partInfo);
            return outputMessage;
        }
        else
        {
            return null;
        }

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

}
