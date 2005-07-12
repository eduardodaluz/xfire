package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Reads in the message body using the service binding.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DispatchServiceHandler
    extends AbstractHandler
{
    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws XFireFault
    {
        context.getInPipeline().addHandlers(context.getService().getInHandlers());

        if (context.getExchange().hasOutMessage())
        {
            HandlerPipeline pipeline = new HandlerPipeline(context.getXFire().getOutPhases());
            pipeline.addHandlers(context.getService().getOutHandlers());
            pipeline.addHandlers(context.getXFire().getOutHandlers());
            OutMessage msg = context.getExchange().getOutMessage();
            pipeline.addHandlers(msg.getChannel().getTransport().getOutHandlers());
            
            context.setOutPipeline(pipeline);
        }
    }
}
