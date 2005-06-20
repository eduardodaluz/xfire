package org.codehaus.xfire.client;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.AbstractMessageExchange;
import org.codehaus.xfire.fault.XFireFault;

public class ClientInOutExchange
    extends AbstractMessageExchange
{
    private Client client;
    private MessageContext context;
    
    public ClientInOutExchange(MessageContext context, Client client)
    {
        this.client = client;
        this.context = context;
    }

    public void doExchange()
    {
        client.receive(context.getInMessage().getBody());
    }

    public void handleFault(XFireFault fault)
    {
        client.receiveFault(fault);
    }
}
