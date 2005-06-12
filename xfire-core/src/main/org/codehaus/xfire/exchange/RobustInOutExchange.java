package org.codehaus.xfire.exchange;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.transport.Channel;

/**
 * A robust in-out MEP. A reply is always sent back. If a fault occurs
 * it is also sent back.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class RobustInOutExchange
    extends AbstractMessageExchange
{
    private static final Log logger = LogFactory.getLog(RobustInOutExchange.class);
    
    private MessageContext context;
    private InMessage inMessage;
    private OutMessage outMessage;
    public RobustInOutExchange(MessageContext context)
    {
        this.context = context;
        this.inMessage = context.getInMessage();
        
        context.setExchange(this);
    }

    public InMessage createInMessage()
    {
        throw new IllegalStateException("In messages can't be created from this MEP.");
    }
    
    public OutMessage createOutMessage()
    {
        OutMessage outMessage = new OutMessage(Channel.BACKCHANNEL_URI);
        outMessage.setChannel(getOutChannel());
        outMessage.setSoapVersion(getInMessage().getSoapVersion());

        return outMessage;
    }

    public OutMessage createOutFault()
    {
        return createOutMessage();
    }

    public Channel getOutChannel()
    {
        return getInMessage().getChannel();
    }

    public Channel getOutFaultChannel()
    {
        return getOutChannel();
    }
    
    public InMessage getInMessage()
    {
        return inMessage;
    }

    public OutMessage getOutMessage()
    {
        return outMessage;
    }

    public void setOutMessage(OutMessage outMessage)
    {
        this.outMessage = outMessage;
    }

    public void doExchange()
    {
        try
        {
            // In pipeline
            invokeInPipeline(context);

            OutMessage outMsg = createOutMessage();
            context.setOutMessage(outMsg);
            
            Handler binding = context.getService().getBinding();
            binding.invoke(context);

            // The out pipeline
            invokeOutPipeline(context);

            MessageSerializer serializer = context.getService().getBinding();
            outMsg.setSerializer(serializer);
            
            Channel channel = getOutChannel();
            channel.send(context, outMsg);
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
        
        OutMessage outMsg = createOutFault();
        outMsg.setBody(fault);
        outMsg.setSerializer(context.getService().getFaultSerializer());
        context.setOutMessage(outMsg);

        handleFault(fault, context);
        invokeFaultPipeline(fault, context);
        
        Channel channel = getOutChannel();
        try
        {
            channel.send(context, outMsg);
        }
        catch (XFireException e)
        {
            logger.error("Exception occurred while sending fault.", e);
        }
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
