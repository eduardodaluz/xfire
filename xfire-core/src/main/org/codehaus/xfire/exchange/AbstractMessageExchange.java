package org.codehaus.xfire.exchange;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.transport.Transport;

public abstract class AbstractMessageExchange
    implements MessageExchange
{
    private OperationInfo operation;

    protected void invokeInPipeline(MessageContext context)
        throws Exception
    {
        Transport transport = context.getInMessage().getChannel().getTransport();
        if (transport != null)
            HandlerPipeline.invokePipeline(transport.getRequestPipeline(), context);
    
        if (context.getService() != null)
            HandlerPipeline.invokePipeline(context.getService().getInPipeline(), context);
    }

    protected void invokeOutPipeline(MessageContext context)
        throws Exception
    {
        Transport transport = context.getOutMessage().getChannel().getTransport();
        if (context.getService() != null)
            HandlerPipeline.invokePipeline(context.getService().getOutPipeline(), context);
    
        if (transport != null)
            HandlerPipeline.invokePipeline(transport.getResponsePipeline(), context);
    }

    protected void invokeFaultPipeline(XFireFault fault, MessageContext context)
    {
        Transport transport = context.getOutMessage().getChannel().getTransport();
        
        if (transport != null && transport.getFaultPipeline() != null)
        {
            transport.getFaultPipeline().handleFault(fault, context);
        }
    
        if (context.getService() != null && context.getService().getFaultPipeline() != null)
        {
            context.getService().getFaultPipeline().handleFault(fault, context);
        }
    }

    public OperationInfo getOperation()
    {
        return operation;
    }

    public void setOperation(OperationInfo operation)
    {
        this.operation = operation;
    }
}
