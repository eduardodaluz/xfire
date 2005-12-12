package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.transport.Transport;

/**
 * Finds the appropriate binding to use when invoking a service. This is done by
 * comparing the Transport on the InMessage to the Transport on the Binding.
 * If they are equal, that binding is used.
 * 
 * @author Dan Diephouse
 */
public class LocateBindingHandler
    extends AbstractHandler
{
    public LocateBindingHandler()
    {
        super();

        after(ReadHeadersHandler.class.getName());
    }

    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        if (context.getBinding() != null) return;
        
        Transport t = context.getInMessage().getChannel().getTransport();
        Service service = context.getService();
        
        // find a binding with that soap binding id
        // set the binding
        Binding binding = t.findBinding(context, service);
        
        context.setBinding(binding);
    }
}