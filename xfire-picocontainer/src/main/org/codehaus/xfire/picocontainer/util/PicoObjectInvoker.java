package org.codehaus.xfire.picocontainer.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.object.Invoker;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;

public class PicoObjectInvoker
    implements Invoker
{

    private static final Log logger = LogFactory.getLog(Invoker.class.getName());

    private ObjectReference picoReference;

    private Object componentKey;

    public PicoObjectInvoker(ObjectReference picoReference, Object componentKey)
    {
        this.picoReference = picoReference;
        this.componentKey = componentKey;
    }

    public Object invoke(final Method method, final Object[] params, final MessageContext context)
        throws XFireFault
    {
        try
        {
            PicoContainer pico = (PicoContainer) picoReference.get();
            final Object serviceObject = pico.getComponentInstance(componentKey);

            return method.invoke(serviceObject, params);
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireFault("Illegal argument.", e, XFireFault.SENDER);
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
                logger.warn("Error invoking service.", t);
                throw new XFireFault(t, XFireFault.SENDER);
            }
            else
            {
                logger.warn("Error invoking service.", e);
                throw new XFireRuntimeException("Error invoking service.", e);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new XFireFault("Couldn't access service object.", e, XFireFault.RECEIVER);
        }
    }
}
