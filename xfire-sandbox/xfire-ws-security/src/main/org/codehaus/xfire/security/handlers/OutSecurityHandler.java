package org.codehaus.xfire.security.handlers;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.OutSecurityProcessor;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class OutSecurityHandler
    extends AbstractHandler
{
    private OutSecurityProcessor processor;


    public OutSecurityProcessor getProcessor()
    {
        return processor;
    }


    public void setProcessor(OutSecurityProcessor processor)
    {
        this.processor = processor;
    }


    public void invoke(MessageContext context)
        throws Exception
    {
        OutMessage message = context.getOutMessage();
        message.setSerializer(new OutSecuritySerializer(message.getSerializer(),getProcessor()));

        
    }

}
