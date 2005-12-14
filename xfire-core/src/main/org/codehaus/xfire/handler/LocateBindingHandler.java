package org.codehaus.xfire.handler;

import java.util.Iterator;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.transport.Channel;

/**
 * Finds the appropriate binding to use when invoking a service. This is delegated
 * to the transport via the findBinding method.
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
        
        Channel c = context.getInMessage().getChannel();
        Service service = context.getService();
        
        // find a binding with that soap binding id
        // set the binding
        Binding binding =  c.getTransport().findBinding(context, service);
        
        if (!binding.isUndefinedEndpointAllowed())
        {
            boolean defined = false;
            for (Iterator itr = service.getEndpoints().iterator(); itr.hasNext();)
            {
                if (((Endpoint) itr.next()).getUrl().equals(c.getUri()))
                {
                    defined = true;
                    break;
                }
            }
            
            if (!defined)
            {
                throw new XFireFault("Invalid endpoint for service.", XFireFault.SENDER);
            }
        }
        
        context.setBinding(binding);
    }
}