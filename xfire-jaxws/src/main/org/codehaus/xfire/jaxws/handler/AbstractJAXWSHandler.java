package org.codehaus.xfire.jaxws.handler;

import java.util.List;

import javax.xml.ws.handler.Handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.jaxws.JAXWSHelper;
import org.codehaus.xfire.jaxws.PortInfo;
import org.codehaus.xfire.jaxws.ServiceDelegate;
import org.codehaus.xfire.jaxws.binding.AbstractBinding;
import org.codehaus.xfire.transport.Transport;

public abstract class AbstractJAXWSHandler
    extends AbstractHandler
{
    private ServiceDelegate service;
    
    public AbstractJAXWSHandler(ServiceDelegate service)
    {
        super();
        setPhase(Phase.USER);
        this.service = service;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Transport t = context.getInMessage().getChannel().getTransport();
        AbstractBinding binding = JAXWSHelper.getInstance().getBinding(t);
        
        PortInfo portInfo = new PortInfo(context.getBinding().getBindingId(), null, service.getServiceName());
       
        List<Handler> handlers = service.getHandlerResolver().getHandlerChain(portInfo);
        
        SOAPMessageContext soapContext = new SOAPMessageContext(context);
        
        for (Handler handler : handlers)
        {
            invokeHandler(soapContext, handler);
        }
    }

    protected abstract void invokeHandler(SOAPMessageContext soapContext, Handler handler);
    
}
