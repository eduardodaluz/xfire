package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;

public abstract class AbstractChannel
    implements Channel
{
    private ChannelEndpoint receiver;
    private Transport transport;
    private String uri;

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public void setEndpoint(ChannelEndpoint receiver)
    {
        this.receiver = receiver; 
    }

    public ChannelEndpoint getReceiver()
    {
        return receiver;
    }

    public void receive(MessageContext context, InMessage message)
    {
        message.setChannel(this);
        
        getReceiver().onReceive(context, message);
    }

    public Transport getTransport()
    {
        return transport;
    }

    public void setTransport(Transport transport)
    {
        this.transport = transport;
    }
}
