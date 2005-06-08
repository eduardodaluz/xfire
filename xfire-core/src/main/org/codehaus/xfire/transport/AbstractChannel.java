package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.service.Service;

public abstract class AbstractChannel
    implements Channel
{
    private ChannelEndpoint receiver;
    private Transport transport;
    private String uri;
    private Service service;
    
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

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }
}
