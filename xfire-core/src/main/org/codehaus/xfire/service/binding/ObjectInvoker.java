package org.codehaus.xfire.service.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.util.ServiceUtils;

/**
 * An invoker which instantiates classes automatically based on the Service's scope.
 * The default scope is SCOPE_APPLICATION, which creates once instance to use
 * for the lifetime of the invoker.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public class ObjectInvoker
        implements Invoker
{
    /**
     * Scope to denote that a single implementation instance should exist for the lifetime
     * of this invoker and should be reused for every request.
     */
    public final static int SCOPE_APPLICATION = 1;

    /**
     * Scope to denote that a new instance of the service implementation should be created
     * on every call.
     */
    public final static int SCOPE_REQUEST = 3;

    /**
     * Scope for storing one object per session. An example of a session implementation is
     * an http session, whereby one object is created per session and stored in the session scope.
     */
    public final static int SCOPE_SESSION = 2;

    /**
     * Constant to denote the implementation class for the service.
     */
    public static final String SERVICE_IMPL_CLASS = "xfire.serviceImplClass";

    private int scope = ObjectInvoker.SCOPE_APPLICATION;

    /**
     * The object if the scope is SCOPE_APPLICATION.
     */
    private Object appObj;

    public Object invoke(final Method method, final Object[] params, final MessageContext context)
            throws XFireFault
    {
        try
        {
            final Object serviceObject = getServiceObject(context);

            Object[] newParams = params;
            for (int i = 0; i < method.getParameterTypes().length; i++)
            {
                if (method.getParameterTypes()[i].equals(MessageContext.class))
                {
                    newParams = new Object[params.length+1];
                    
                    for (int j = 0; j < newParams.length; j++)
                    {
                        if (j == i)
                        {
                            newParams[j] = context;
                        }
                        else if (j > i)
                        {
                            newParams[j] = params[j-1];
                        }
                        else
                        {
                            newParams[j] = params[j];
                        }
                    }
                }
            }
            
            Method m = matchMethod(method, serviceObject);
            return m.invoke(serviceObject, newParams);
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireFault("Illegal argument invoking '" + ServiceUtils.getMethodName(method) + "': " + e.getMessage(), e, XFireFault.SENDER);
        }
        catch (InvocationTargetException e)
        {
            final Throwable t = e.getTargetException();

            if (t instanceof XFireFault)
            {
                throw (XFireFault) t;
            }
            else if (t instanceof Exception)
            {
                throw new XFireFault(t, XFireFault.SENDER);
            }
            else
            {
                throw new XFireRuntimeException("Error invoking '" + ServiceUtils.getMethodName(method) + '\'', e);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new XFireFault("Couldn't access service object to invoke '" + ServiceUtils.getMethodName(method) + "': " + e.getMessage(), e, XFireFault.RECEIVER);
        }
    }

    /**
     * Creates and returns a service object depending on the scope.
     */
    public Object getServiceObject(final MessageContext context)
            throws XFireFault
    {
        final Service service = context.getService();
        
        if (scope == ObjectInvoker.SCOPE_APPLICATION)
        {
            if (appObj == null)
            {
                synchronized (Service.class)
                {
                    appObj = createServiceObject(service);
                }
            }
            return appObj;
        }
        else if (scope == ObjectInvoker.SCOPE_SESSION)
        {
            final Session session = context.getSession();
            final String key = "service." + service.getSimpleName();

            Object sessObj = session.get(key);
            if (sessObj == null)
            {
                synchronized (Service.class)
                {
                    sessObj = createServiceObject(service);
                    session.put(key, sessObj);
                }
            }
            return sessObj;
        }
        else if (scope == ObjectInvoker.SCOPE_REQUEST)
        {
            return createServiceObject(service);
        }
        else
        {
            throw new UnsupportedOperationException("Scope " + scope + " is invalid.");
        }
    }

    /**
     * Override this method to control how XFire creates the service object.
     *
     * @param service
     * @return
     * @throws XFireFault
     */
    public Object createServiceObject(final Service service)
            throws XFireFault
    {
        try
        {
            Class svcClass = (Class) service.getProperty(ObjectInvoker.SERVICE_IMPL_CLASS);

            if (svcClass == null)
            {
                svcClass = service.getServiceInfo().getServiceClass();
                if(svcClass.isInterface())
                {
                    throw new XFireFault("ObjectInvoker.SERVICE_IMPL_CLASS not set for interface '" + svcClass.getName() + "'", XFireFault.RECEIVER);
                }
            }
          
            if(svcClass.isInterface())
            {
                throw new XFireFault("Service class '" + svcClass.getName() + "' is an interface", XFireFault.RECEIVER);
            }
          
          if(Modifier.isAbstract(svcClass.getModifiers()))
          {
              throw new XFireFault("Service class '" + svcClass.getName() + "' is abstract", XFireFault.RECEIVER);
          }
            return svcClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new XFireFault("Couldn't instantiate service object.", e, XFireFault.RECEIVER);
        }
        catch (IllegalAccessException e)
        {
            throw new XFireFault("Couldn't access service object.", e, XFireFault.RECEIVER);
        }
    }
    
    /**
     * Returns a Method that has the same declaring class as the
     * class of targetObject to avoid the IllegalArgumentException
     * when invoking the method on the target object. The methodToMatch
     * will be returned if the targetObject doesn't have a similar method.
     * 
     * @param methodToMatch The method to be used when finding a matching
     *                      method in targetObject
     * @param targetObject  The object to search in for the method. 
     * @return The methodToMatch if no such method exist in the class of
     *         targetObject; otherwise, a method from the class of
     *         targetObject matching the matchToMethod method.
     */
    private Method matchMethod(Method methodToMatch, Object targetObject) {
        if (isJdkDynamicProxy(targetObject)) {
            Class[] interfaces = targetObject.getClass().getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Method m = getMostSpecificMethod(methodToMatch, interfaces[i]);
                if (!methodToMatch.equals(m)) {
                    return m;
                }
            }
        }
        return methodToMatch;
    }

    /**
     * Return whether the given object is a J2SE dynamic proxy.
     * 
     * @param object the object to check
     * @see java.lang.reflect.Proxy#isProxyClass
     */
    public boolean isJdkDynamicProxy(Object object) {
        return (object != null && Proxy.isProxyClass(object.getClass()));
    }

    /**
     * Given a method, which may come from an interface, and a targetClass
     * used in the current AOP invocation, find the most specific method
     * if there is one. E.g. the method may be IFoo.bar() and the target
     * class may be DefaultFoo. In this case, the method may be
     * DefaultFoo.bar(). This enables attributes on that method to be found.
     * 
     * @param method method to be invoked, which may come from an interface
     * @param targetClass target class for the curren invocation. May
     *        be <code>null</code> or may not even implement the method.
     * @return the more specific method, or the original method if the
     *         targetClass doesn't specialize it or implement it or is null
     */
    public Method getMostSpecificMethod(Method method, Class targetClass) {
        if (method != null && targetClass != null) {
            try {
                method = targetClass.getMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException ex) {
                // Perhaps the target class doesn't implement this method:
                // that's fine, just use the original method
            }
        }
        return method;
    }
    
    public int getScope()
    {
        return scope;
    }

    public void setScope(int scope)
    {
        this.scope = scope;
    }
}
