package org.codehaus.xfire.exchange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
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

    public RobustInOutExchange(MessageContext context)
    {
        super(context);

        if (context.getExchange() != null)
        {
            setInMessage(context.getExchange().getInMessage());
        }
        
        context.setExchange(this);
    }

    public OutMessage getOutMessage()
    {
        if (super.getOutMessage() == null)
        {
            OutMessage outMessage = new OutMessage(Channel.BACKCHANNEL_URI);
            outMessage.setChannel(getOutChannel());
            outMessage.setSoapVersion(getInMessage().getSoapVersion());
    
            setOutMessage(outMessage);
        }
        return super.getOutMessage();
    }

    public AbstractMessage getFaultMessage()
        throws UnsupportedOperationException
    {
        if (super.getFaultMessage() == null)
        {
            OutMessage outMessage = new OutMessage(Channel.BACKCHANNEL_URI);
            outMessage.setChannel(getFaultChannel());
            outMessage.setSoapVersion(getInMessage().getSoapVersion());
    
            setFaultMessage(outMessage);
        }
        return super.getFaultMessage();
    }

    public boolean hasFaultMessage()
    {
        return true;
    }

    public boolean hasInMessage()
    {
        return true;
    }

    public boolean hasOutMessage()
    {
        return true;
    }

    public Channel getFaultChannel()
    {
        return getInChannel();
    }

    public Channel getInChannel()
    {
        return getContext().getInMessage().getChannel();
    }

    public Channel getOutChannel()
    {
        return getInChannel();
    }
}
