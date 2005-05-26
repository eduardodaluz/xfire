package org.codehaus.xfire.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.Transport;

/**
 * A <code>ChannelEndpoint</code> which executes the in pipeline
 * on the service and starts a <code>MessageExchange</code>.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ServiceEndpoint
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(ServiceEndpoint.class);
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        context.setInMessage(msg);
        
        try
        {
            // In pipeline
            invokeInPipeline(context);
        }
        catch (Exception e)
        {
            log.error("Couldn't invoke in pipeline. Aborting receive.", e);
            return;
        }
        
        try
        {
            MessageSerializer serializer = context.getService().getSerializer();
            serializer.readMessage(context.getInMessage(), context);
        }
        catch (Exception e)
        {
            log.error("Couldn't read message. Aborting receive.", e);
            return;
        }

        context.getExchange().doExchange();
    }
 
    protected void invokeInPipeline(MessageContext context)
        throws Exception
    {
        Transport transport = context.getInMessage().getChannel().getTransport();
        if (transport != null)
            HandlerPipeline.invokePipeline(transport.getRequestPipeline(), context);

        if (context.getService() != null)
            HandlerPipeline.invokePipeline(context.getService().getInPipeline(), context);
    }
}
