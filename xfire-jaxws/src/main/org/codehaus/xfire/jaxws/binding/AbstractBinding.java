package org.codehaus.xfire.jaxws.binding;

import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import org.codehaus.xfire.transport.Transport;

public class AbstractBinding implements Binding
{
    private List<Handler> handlerChain;
    private Transport transport;
    
    public AbstractBinding(Transport t)
    {
        super();
        this.transport = t;
    }

    public List<Handler> getHandlerChain()
    {
        return handlerChain;
    }

    public void setHandlerChain(List<Handler> handlerChain)
    {
        this.handlerChain = handlerChain;
    }

    public Transport getTransport()
    {
        return transport;
    }
}
