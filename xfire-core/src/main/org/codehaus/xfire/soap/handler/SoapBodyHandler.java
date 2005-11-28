package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.DispatchServiceHandler;
import org.codehaus.xfire.handler.LocateBindingHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.SoapBinding;

/**
 * Takes the SoapBinding from the MessageContext, selects an appropriate MessageSerailizer
 * and reads in the soap body.
 * @author Dan Diephouse
 */
public class SoapBodyHandler
    extends AbstractHandler
{   
    public SoapBodyHandler()
    {
        super();
        
        after(LocateBindingHandler.class.getName());
        before(DispatchServiceHandler.class.getName());
    }

    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        SoapBinding binding = (SoapBinding) context.getBinding();
        
        MessageSerializer ser = binding.getSerializer(context.getExchange().getOperation());

        ser.readMessage(context.getInMessage(), context);
    }
}