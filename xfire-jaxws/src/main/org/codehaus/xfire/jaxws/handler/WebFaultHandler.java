package org.codehaus.xfire.jaxws.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.handler.CustomFaultHandler;
import org.codehaus.xfire.service.MessagePartInfo;

/**
 * Takes an exception that is a JAX-WS {@lit @}WebFault and serializes it
 * appropriately via JAXB.
 * 
 * @author Dan Diephouse
 */
public class WebFaultHandler
    extends CustomFaultHandler
{
    protected Object getFaultBean(Throwable cause, MessagePartInfo faultPart, MessageContext context) 
    {
        try
        {
            Method method = cause.getClass().getMethod("getFaultInfo", new Class[0]);
            return method.invoke(cause, new Object[0]);
        }
        catch (InvocationTargetException e)
        {
            throw new XFireRuntimeException("Couldn't invoke getFaultInfo method.", e);
        }
        catch (NoSuchMethodException e)
        {
            return cause;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't access getFaultInfo method.", e);
        }
    }
}
