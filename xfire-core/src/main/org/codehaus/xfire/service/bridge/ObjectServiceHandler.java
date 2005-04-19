package org.codehaus.xfire.service.bridge;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.EndpointHandler;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.service.binding.Operation;

/**
 * Handles java services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public class ObjectServiceHandler
    extends AbstractHandler
    implements EndpointHandler
{
    private static final Log logger = LogFactory.getLog(ObjectServiceHandler.class.getName());

    public static final String NAME = "java";

    public static final String RESPONSE_VALUE = "xfire.java.response";

    public static final String RESPONSE_PIPE = "xfire.java.responsePipe";

    private final Class bridgeClass;
    
    public ObjectServiceHandler()
    {
        bridgeClass = null;
    }
    
    public ObjectServiceHandler(final Class bridge)
    {
        this.bridgeClass = bridge;
    }

    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke(final MessageContext context)
        throws XFireFault
    {
        try
        {
            final MessageBridge pipe = getMessageBridge(context);
            
            // Read in the parameters...
            final List params = pipe.read();

            // Don't read the operation in until after reading. Otherwise
            // it won't work for document style services.
            final Operation operation = pipe.getOperation();

            final Invoker invoker = ((ObjectService) context.getService()).getInvoker();
            
            // invoke the service method...
            if (!operation.isAsync())
            {
                final Object value = invoker.invoke(operation.getMethod(), params.toArray(), context);

                context.setProperty(RESPONSE_VALUE, value);
                context.setProperty(RESPONSE_PIPE, pipe);
            }
            else
            {
                Runnable runnable = new Runnable() 
                {
                    public void run() 
                    {
                        try
                        {
                            invoker.invoke(operation.getMethod(), params.toArray(), context);
                        }
                        catch (XFireFault e)
                        {
                            context.getService().getFaultHandler().handleFault(e, context);
                        }
                    }
                };
                
                Thread opthread = new Thread(runnable);
                opthread.start();
            }
        }
        catch (XFireRuntimeException e)
        {
            logger.warn("Error invoking service.", e);
            throw new XFireFault("Error invoking service.", e, XFireFault.SENDER);
        }
    }

    /**
     * @return
     * @throws XFireFault 
     */
    protected MessageBridge getMessageBridge(MessageContext context) 
        throws XFireFault
    {
        if (bridgeClass != null)
        {
            try
            {
                Constructor constructor =
                    bridgeClass.getConstructor(new Class[] {MessageContext.class});
                
                return (MessageBridge) constructor.newInstance( new Object[] { context } );
            }
            catch (Exception e)
            {
                logger.error("Couldn't create message bridge.", e);
                throw new XFireFault("Couldn't create message bridge", e, XFireFault.RECEIVER);
            }
            
        }
        return MessageBridgeFactory.createMessageBridge(context);
    }

    public void writeResponse(final MessageContext context)
        throws XFireFault
    {
        final MessageBridge pipe = (MessageBridge) context.getProperty(RESPONSE_PIPE);
        final Object value = context.getProperty(RESPONSE_VALUE);

        if (pipe != null)
        {
            pipe.write(new Object[] { value });
        }
    }

    public boolean hasResponse(MessageContext context)
    {
        return (context.getProperty(RESPONSE_PIPE) != null);
    }
}
