package org.codehaus.xfire.fault;

import org.codehaus.xfire.MessageContext;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AbstractFaultHandler
    implements FaultHandler
{
    /**
     * @param fault
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context)
    {
        if (context.getTransport() != null && context.getTransport().getFaultPipeline() != null)
        {
            context.getTransport().getFaultPipeline().handleFault(fault, context);
        }

        if (context.getService() != null && context.getService().getFaultPipeline() != null)
        {
            context.getService().getFaultPipeline().handleFault(fault, context);
        }
    }
}
