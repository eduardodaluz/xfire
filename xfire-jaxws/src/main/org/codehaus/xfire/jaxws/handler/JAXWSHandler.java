package org.codehaus.xfire.jaxws.handler;

import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.jaxws.JAXWSHelper;
import org.codehaus.xfire.jaxws.PortInfo;
import org.codehaus.xfire.jaxws.ServiceDelegate;
import org.codehaus.xfire.jaxws.binding.AbstractBinding;
import org.codehaus.xfire.transport.Transport;

public class JAXWSHandler
    extends AbstractHandler
{
    private ServiceDelegate service;
    
    public JAXWSHandler(ServiceDelegate service)
    {
        super();
        
        this.service = service;
    }

    @Override
    public String getPhase()
    {
        return Phase.USER;
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
            if (handler instanceof LogicalHandler)
            {
                LogicalHandler lh = (LogicalHandler) handler;
                
            }
            else if (handler instanceof SOAPHandler)
            {
                SOAPHandler sh = (SOAPHandler) handler;

                sh.handleMessage(soapContext);
            }
            else
            {
                handler.handleMessage(soapContext);
            }
        }
    }
}
