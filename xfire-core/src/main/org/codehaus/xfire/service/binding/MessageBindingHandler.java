package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.EndpointHandler;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;

/**
 * Handles java services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public class MessageBindingHandler
    extends AbstractHandler
    implements EndpointHandler
{
    private static final Log logger = LogFactory.getLog(MessageBindingHandler.class.getName());

    public static final String RESPONSE_VALUE = "xfire.message.response";

    public static final String RESPONSE_OP = "xfire.message.operation";
    
    public MessageBindingHandler()
    {
    }

    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke(final MessageContext context)
        throws XFireFault
    {
        try
        {
            final Service service = context.getService();
            final OperationInfo operation = (OperationInfo) service.getOperations().iterator().next();
            final Invoker invoker = context.getService().getInvoker();

            context.setProperty(RESPONSE_OP, operation);
            
            final List params = new ArrayList();
            
            for (Iterator itr = operation.getInputMessage().getMessageParts().iterator(); itr.hasNext();)
            {
                MessagePartInfo p = (MessagePartInfo) itr.next();
                
                params.add( service.getBindingProvider().readParameter(p, context) );
            }
            
            // invoke the service method...
            if (!operation.isOneWay())
            {
                Object value = invoker.invoke(operation.getMethod(), params.toArray(), context);
                
                context.setProperty(RESPONSE_VALUE, value);
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

    public void writeResponse(final MessageContext context)
        throws XFireFault
    {
        final Object value = context.getProperty(RESPONSE_VALUE);
        final OperationInfo op = (OperationInfo) context.getProperty(RESPONSE_OP);

        if (value != null)
        {
            MessageInfo outMsg = op.getOutputMessage();
            MessagePartInfo outP = (MessagePartInfo) outMsg.getMessageParts().iterator().next();
            
            context.getService().getBindingProvider().writeParameter(outP, context, value);
        }
    }

    public boolean hasResponse(MessageContext context)
    {
        return (context.getProperty(RESPONSE_VALUE) != null);
    }
}
