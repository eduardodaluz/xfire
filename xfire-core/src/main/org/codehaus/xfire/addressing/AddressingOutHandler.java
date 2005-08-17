package org.codehaus.xfire.addressing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;

public class AddressingOutHandler
    extends AbstractHandler
{
    private final static Log logger = LogFactory.getLog(AddressingOutHandler.class);
    
    public AddressingOutHandler()
    {
    }

    public String getPhase()
    {
        return Phase.TRANSPORT;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        OutMessage msg = context.getOutMessage();
        AddressingHeaders headers = (AddressingHeaders) msg.getProperty(AddressingInHandler.ADRESSING_HEADERS);
        AddressingHeadersFactory factory = (AddressingHeadersFactory) msg.getProperty(AddressingInHandler.ADRESSING_FACTORY);
        
        if (headers == null)
        {
            logger.debug("Couldn't find adressing headers.");
            return;
        }
        
        if (msg == null)
        {
            logger.warn("There was no out message!");
            return;
        }
        
        factory.writeHeaders(msg.getHeader(), headers);
    }
}