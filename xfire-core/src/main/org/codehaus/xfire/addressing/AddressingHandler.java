package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.yom.Element;

public class AddressingHandler
    extends AbstractHandler
{
    public static final Object ADRESSING_HEADERS = "xfire-ws-adressing-headers";

    public static final Object ADRESSING_FACTORY = "xfire-ws-adressing-factory";
    
    private List factories = new ArrayList();
    
    public AddressingHandler()
    {
        createFactories();
    }
    
    public void createFactories()
    {
        factories.add(new AddressingHeadersFactory200502());
    }
    
    public String getPhase()
    {
        return Phase.PRE_DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        for (Iterator itr = factories.iterator(); itr.hasNext();)
        {
            AddressingHeadersFactory factory = (AddressingHeadersFactory) itr.next();
            
            InMessage msg = context.getInMessage();
            Element header = msg.getHeader();
            if (factory.hasHeaders(header))
            {
                AddressingHeaders headers = factory.createHeaders(header);
                msg.setProperty(ADRESSING_HEADERS, headers);
                msg.setProperty(ADRESSING_FACTORY, factory);

                // Dispatch the service
                Service service = getService(headers, context);
                if (service != null) context.setService(service);
                
                // Dispatch the Exchange and operation
                OperationInfo op = 
                    service.getServiceInfo().getOperationByAction(headers.getAction());

                if (op == null)
                {
                    throw new XFireFault("Action '" + headers.getAction() + 
                                             "' was not found for service " + headers.getTo(),
                                         XFireFault.SENDER);
                }
                
                MessageExchange exchange = context.createMessageExchange(op);
                context.setExchange(exchange);
                
                EndpointReference replyTo = headers.getReplyTo();
                if (replyTo != null)
                {
                    String addr = replyTo.getAddress();
                    if (addr != null)
                    {
                        OutMessage outMessage = new OutMessage(addr);
                        outMessage.setSoapVersion(exchange.getInMessage().getSoapVersion());
                        
                        // Find the correct transport for the reply message.
                        Transport t = context.getXFire().getTransportManager().getTransportForUri(addr);
                        if (t == null)
                        {
                            throw new XFireFault("URL was not recognized: " + addr, XFireFault.SENDER);
                        }

                        outMessage.setChannel(t.createChannel());
                        exchange.setOutMessage(outMessage);
                    }
                }
            }
        }
    }
    
    protected Service getService(AddressingHeaders headers, MessageContext context)
    {
        if (headers.getTo() == null) return null;
        
        int i = headers.getTo().lastIndexOf('/');

        return context.getXFire().getServiceRegistry().getService(headers.getTo().substring(i+1));
    }
}