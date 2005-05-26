package org.codehaus.xfire.exchange;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.Handler;

/**
 * An in only MEP. If a fault occurs, it is not sent anywhere, but it
 * is logged.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class InExchange
    extends AbstractMessageExchange
{
    private static final Log logger = LogFactory.getLog(InExchange.class);
    
    private MessageContext context;
    private InMessage inMessage;
    private OutMessage outMessage;
    
    public InExchange(MessageContext context)
    {
        this.context = context;
        this.inMessage = context.getInMessage();
        
        context.setExchange(this);
    }

    public void doExchange()
    {
        try
        {
            validateHeaders(context);
            
            Handler binding = context.getService().getBinding();
            binding.invoke(context);
        }
        catch (Exception e)
        {
            XFireFault fault = XFireFault.createFault(e);
            
            handleFault(fault);
        }
    }

    public void handleFault(XFireFault fault)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Fault occurred.", fault);
        }

        handleFault(fault, context);
        invokeFaultPipeline(fault, context);
    }

    public void handleFault(XFireFault fault, MessageContext context)
    {
        Stack handlerStack = context.getHandlerStack();

        while (!handlerStack.empty())
        {
            Handler handler = (Handler) handlerStack.pop();
            handler.handleFault(fault, context);
        }
    }

}
