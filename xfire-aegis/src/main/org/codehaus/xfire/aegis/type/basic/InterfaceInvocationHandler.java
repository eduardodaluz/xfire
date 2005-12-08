/**
 * 
 */
package org.codehaus.xfire.aegis.type.basic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class InterfaceInvocationHandler
    implements InvocationHandler
{
    InterfaceInvocationHandler()
    {
    }

    private Map attributes = new HashMap();

    public void writeProperty(Object key, Object value)
    {
        attributes.put(key, value);
    }

    public Object readProperty(Object key)
    {
        return attributes.get(key);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        if (Object.class.getMethod("hashCode", null).equals(method))
        {
            return proxyHashCode(proxy);
        }
        else if (Object.class.getMethod("equals", new Class[] { Object.class }).equals(method))
        {
            return proxyEquals(proxy, args[0]);
        }
        else if (Object.class.getMethod("toString", null).equals(method))
        {
            return proxyToString(proxy);
        }
        else
        {
            if (isGetterMethod(method, args))
                return doGetter(method, args);
            else if (isSetterMethod(method, args))
                return doSetter(method, args);
            else
                throw new IllegalAccessError(method.getName() + " is not delegated.");
        }
    }

    protected boolean isGetterMethod(Method method, Object[] args)
    {
        if (args != null && args.length > 0)
            return false;

        String methodName = method.getName();

        if (methodName.startsWith("get") && methodName.length() > 3)
            return true;
        else if (methodName.startsWith("is") && methodName.length() > 2)
            return true;
        /*
         * // should "hasXXX()" be considered a getter method? else if
         * (methodName.startsWith("has") && methodName.length() > 3) { return
         * true; }
         */
        else
            return false;
    }

    protected boolean isSetterMethod(Method method, Object[] args)
    {
        if (args == null || args.length != 1)
            return false;

        if (!"void".equals(method.getReturnType().getName()))
            return false;

        String methodName = method.getName();

        if (methodName.startsWith("set") && methodName.length() > 3)
            return true;
        else
            return false;
    }

    protected Object doGetter(Method method, Object[] args)
        throws Throwable
    {
        String methodName = method.getName();
        String attrName = null;

        if (methodName.startsWith("get"))
            attrName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        else if (methodName.startsWith("is"))
            attrName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        else
            throw new IllegalAccessError(methodName + " is not a valid getter method.");

        return readProperty(attrName);
    }

    protected Object doSetter(Method method, Object[] args)
        throws Throwable
    {
        String methodName = method.getName();
        String attrName = null;

        if (methodName.startsWith("set"))
            attrName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        else
            throw new IllegalAccessError(methodName + " is not a valid setter method.");

        writeProperty(attrName, args[0]);

        return null;
    }

    protected Integer proxyHashCode(Object proxy)
    {
        return new Integer(System.identityHashCode(proxy));
    }

    protected Boolean proxyEquals(Object proxy, Object other)
    {
        return proxy == other ? Boolean.TRUE : Boolean.FALSE;
    }

    protected String proxyToString(Object proxy)
    {
        return proxy.getClass().getName() + '@' + proxy.hashCode();
    }
}