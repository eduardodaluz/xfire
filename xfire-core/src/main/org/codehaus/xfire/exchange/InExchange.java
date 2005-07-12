package org.codehaus.xfire.exchange;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.transport.Channel;

/**
 * An in only MEP. If a fault occurs, it is not sent anywhere, but it
 * is logged.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class InExchange
    extends AbstractMessageExchange
{
    public InExchange(MessageContext context)
    {
        super(context);

        if (context.getExchange() != null)
        {
            setInMessage(context.getInMessage());
        }
        
        context.setExchange(this);
    }

    public Channel getInChannel()
    {
        return getContext().getInMessage().getChannel();
    }

    public boolean hasInMessage()
    {
        return true;
    }
}
