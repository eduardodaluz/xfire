package org.codehaus.xfire.plexus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.java.JavaInvoker;

/**
 * Invokes a Plexus service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 18, 2004
 */
public class ServiceInvoker
	extends JavaInvoker
{
    private static Logger logger = Logger.getLogger(ServiceInvoker.class.getName());
    
    private ServiceLocator locator;
    
    private boolean hasService = true;
    private boolean hasChecked = false;
    
    public ServiceInvoker( ServiceLocator locator )
    {
        this.locator = locator;
    }
    
    public Object invoke(Method method, Object[] args, MessageContext context)
        throws XFireFault
    {
        if ( !hasChecked )
        {
            if ( locator.hasComponent(method.getDeclaringClass().getName(),
                                      context.getServiceName()) )
            {
                hasService = true;
            }
            else
            {
                hasService = false;
            }
        }

        if ( hasService )
        {
            try
            {
	            Object component = locator.lookup(
	                    method.getDeclaringClass().getName(),
	                    context.getServiceName());
	            
	            return method.invoke(component, args);
            }
            catch (IllegalArgumentException e)
            {
                throw new XFireFault( "Illegal argument.", e, XFireFault.SENDER );
            }
            catch (InvocationTargetException e)
            {
                Throwable t = e.getTargetException();
                t.getMessage();
                
                if ( t instanceof XFireFault )
                {
                    throw (XFireFault) t;
                }
                else if ( t instanceof Exception )
                {
                    logger.warn("Error invoking service.", t);
                    throw new XFireFault( (Exception) t, XFireFault.SENDER );
                }
                else
                {
                    logger.warn("Error invoking service.", e);
                    throw new XFireRuntimeException( "Error invoking service.", e );
                }
            }
            catch (IllegalAccessException e)
            {
                throw new XFireFault( "Couldn't access service object.", e, XFireFault.RECEIVER );
            } 
            catch (ComponentLookupException e)
            {
                throw new XFireFault( "Couldn't find plexus service.", e, XFireFault.RECEIVER );
            }
        }
        else
        {
            return super.invoke(method, args, context);
        }
    }

}
