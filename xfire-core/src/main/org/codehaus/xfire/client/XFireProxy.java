package org.codehaus.xfire.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.http.SoapHttpClient;
import org.codehaus.xfire.fault.XFireFault;

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
    private URL url;

    XFireProxy(URL url)
    {
        this.url = url;
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
            result = handleHttpRequest(methodName, args);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Result [" + String.valueOf(result) + "]");
        }
        return result;
    }

    private Object handleHttpRequest(String methodName, Object[] args)
            throws XFireFault, IOException
    {
        // TODO: use new client library here
        SoapHttpClient soapClient = new SoapHttpClient(null, url.toString());
        soapClient.invoke();
        // TODO: return the result
        return null;
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
            return new Boolean(url.equals(otherClient.getURL()));
        }
        else if (methodName.equals("hashCode") && params.length == 0)
        {
            return new Integer(url.hashCode());
        }
        else if (methodName.equals("toString") && params.length == 0)
        {
            return "XFireProxy[" + url + "]";
        }
        return null;
    }

    /**
     * Returns the client url.
     *
     * @return
     */
    public URL getURL()
    {
        return url;
    }


}

