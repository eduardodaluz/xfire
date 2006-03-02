package org.codehaus.xfire.picocontainer.util;

import org.codehaus.xfire.util.factory.Factory;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * This is a simple adapter from pico container to {@link Factory}
 * <p>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 * Feb 12, 2006 1:19:43 AM
 */
public class PicoFactory implements Factory
{

    //private static final Log logger = LogFactory.getLog(Invoker.class.getName());

    private final ObjectReference picoReference;

    private final Object componentKey;

    public PicoFactory(ObjectReference picoReference, Object componentKey) {
        this.picoReference = picoReference;
        this.componentKey = componentKey;
    }
    public Object create(){
        PicoContainer pico = (PicoContainer) picoReference.get();
        return pico.getComponentInstance(componentKey);
    }
    /*
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
    }*/
}
