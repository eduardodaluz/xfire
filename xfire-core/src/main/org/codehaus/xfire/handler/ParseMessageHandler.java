package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Reads in the message body using the service binding.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ParseMessageHandler
    extends AbstractHandler
{
    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws XFireFault
    {
        context.getService().getBinding().readMessage(context.getInMessage(), context);
    }
}
