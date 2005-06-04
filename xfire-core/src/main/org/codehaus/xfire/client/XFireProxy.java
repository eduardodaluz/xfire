package org.codehaus.xfire.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;

/**
 * Proxy implementation for XFire SOAP clients.  Applications will generally use <code>XFireProxyFactory</code> to
 * create proxy clients.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see XFireProxyFactory#create
 */
public class XFireProxy
        implements InvocationHandler
{
    private static final Log log = LogFactory.getLog(XFireProxy.class);
    private Client client;

    XFireProxy(Client client)
    {
        this.client = client;
    }

    /**
     * Handles the object invocation.
     *
     * @param proxy  the proxy object to invoke
     * @param method the method to call
     * @param args   the arguments to the proxy object
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        String methodName = method.getName();
        Class[] parameterTypes = method.getParameterTypes();
        if (log.isDebugEnabled())
        {
            log.debug("Method [" + methodName + "] " + ((args == null) ? "" : Arrays.asList(args).toString()));
        }
        
        Object result = handleCanonicalMethods(methodName, parameterTypes, args);
        
        if (result == null)
        {
            result = handleRequest(methodName, args);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Result [" + String.valueOf(result) + "]");
        }
        return result;
    }

    private Object handleRequest(String methodName, Object[] args)
            throws XFireFault, IOException
    {
        OperationInfo op = client.getService().getServiceInfo().getOperation(methodName);
        
        return ((Object[]) client.invoke(op, args))[0];
    }

    /**
     * Handles canonical method calls such as <code>equals</code>, <code>hashCode</code>, and <code>toString</code>.
     *
     * @param methodName the method name.
     * @param params     the parameter types.
     * @param args       the arguments
     * @return the result, if <code>methodName</code> is a canonical method; or <code>null</code> if not.
     */
    private Object handleCanonicalMethods(String methodName, Class[] params, Object[] args)
    {
        if (methodName.equals("equals") &&
                params.length == 1
                && params[0].equals(Object.class))
        {
            Object other = args[0];
            if (other == null ||
                    !Proxy.isProxyClass(other.getClass()) ||
                    !(Proxy.getInvocationHandler(other) instanceof XFireProxy))
            {
                return Boolean.FALSE;
            }
            
            XFireProxy otherClient = (XFireProxy) Proxy.getInvocationHandler(other);
            
            return new Boolean(otherClient == this);
        }
        else if (methodName.equals("hashCode") && params.length == 0)
        {
            return new Integer(hashCode());
        }
        else if (methodName.equals("toString") && params.length == 0)
        {
            return "XFireProxy[" + client.getUrl() + "]";
        }
        return null;
    }
}

