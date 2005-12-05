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
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.dead.DeadLetterTransport;
import org.jdom.Attribute;
import org.jdom.Element;

public class AddressingInHandler
    extends AbstractHandler
{
    public static final Object ADRESSING_HEADERS = "xfire-ws-adressing-headers";

    public static final Object ADRESSING_FACTORY = "xfire-ws-adressing-factory";

    private List factories = new ArrayList();

    public AddressingInHandler()
    {
        createFactories();
    }

    public void createFactories()
    {
        factories.add(new AddressingHeadersFactory200508());
        factories.add(new AddressingHeadersFactory200408());

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
                if (service != null)
                {
                    context.setService(service);

                }
                else
                {
                    // wsa:To can be not specified, so use service found by url
                    service = context.getService();
                }

                // Dispatch the Exchange and operation
                AddressingOperationInfo op = AddressingOperationInfo.getOperationByInAction(service
                        .getServiceInfo(), headers.getAction());

                if (op == null)
                {
                    throw new XFireFault("Action '" + headers.getAction()
                            + "' was not found for service " + headers.getTo(), XFireFault.SENDER);
                }

                MessageExchange exchange = context.createMessageExchange(op.getOperationInfo());
                context.setExchange(exchange);

                EndpointReference faultTo = headers.getFaultTo();
                OutMessage faultMsg = null;
                if (faultTo != null)
                {
                    faultMsg = processEPR(context, faultTo, op, headers, factory);
                }
                else
                {
                    faultMsg = createDefaultMessage(context, op, headers, factory);
                }
                exchange.setFaultMessage(faultMsg);

                EndpointReference replyTo = headers.getReplyTo();
                OutMessage outMessage = null;
                if (replyTo != null)
                {
                    outMessage = processEPR(context, replyTo, op, headers, factory);
                }
                else
                {
                    outMessage = createDefaultMessage(context, op, headers, factory);
                }
                exchange.setOutMessage(outMessage);
            }
        }
    }

    private OutMessage createDefaultMessage(MessageContext context,
                                            AddressingOperationInfo aoi,
                                            AddressingHeaders inHeaders,
                                            AddressingHeadersFactory factory)
    {
        OutMessage outMessage = context.getOutMessage();

        AddressingHeaders headers = new AddressingHeaders();
        headers.setTo(factory.getAnonymousUri());

        // TODO: need way to set out action
        headers.setAction(aoi.getOutAction());
        outMessage.setProperty(ADRESSING_HEADERS, headers);
        outMessage.setProperty(ADRESSING_FACTORY, factory);

        return outMessage;
    }

    /**
     * @param factory
     * @param addr
     * @return
     */
    private boolean isNoneAddress(AddressingHeadersFactory factory, String addr)
    {
        return factory.getNoneUri() != null && factory.getNoneUri().equals(addr);
    }

    /**
     * @param context
     * @param epr
     * @param aoi
     * @param inHeaders
     * @param factory
     * @return
     * @throws XFireFault
     * @throws Exception
     */
    protected OutMessage processEPR(MessageContext context,
                                    EndpointReference epr,
                                    AddressingOperationInfo aoi,
                                    AddressingHeaders inHeaders,
                                    AddressingHeadersFactory factory)
        throws XFireFault, Exception
    {
        String addr = epr.getAddress();
        OutMessage outMessage = null;
        

        Transport t = null;
        if (addr == null)
        {
            throw new XFireFault("Invalid ReplyTo address.", XFireFault.SENDER);
        }
        if (addr.equals(factory.getAnonymousUri()))
        {
            outMessage = new OutMessage(Channel.BACKCHANNEL_URI);
            t = context.getInMessage().getChannel().getTransport();
        }
        else

        if (isNoneAddress(factory, addr))
        {
            t = new DeadLetterTransport();
            outMessage = new OutMessage(addr);
        }
        else
        {
            outMessage = new OutMessage(addr);
            t = context.getXFire().getTransportManager().getTransportForUri(addr);
        }

        outMessage.setSoapVersion(context.getExchange().getInMessage().getSoapVersion());

        if (t == null)
        {
            throw new XFireFault("URL was not recognized: " + addr, XFireFault.SENDER);
        }

        outMessage.setChannel(t.createChannel());

        AddressingHeaders headers = new AddressingHeaders();
        headers.setTo(addr);

        headers.setAction(aoi.getOutAction());

        Element refParam = epr.getReferenceParametersElement();
        if (refParam != null)
        {
            List refs = refParam.cloneContent();

            List params = new ArrayList();
            for (int i = 0; i < refs.size(); i++)
            {
                if (refs.get(i) instanceof Element)
                {
                    Element e = (Element) refs.get(i);
                    e.setAttribute(new Attribute(WSAConstants.WSA_IS_REF_PARAMETER, "true", epr
                            .getNamespace()));
                    params.add(e);
                }

                headers.setReferenceParameters(params);
            }
        }

        outMessage.setProperty(ADRESSING_HEADERS, headers);
        outMessage.setProperty(ADRESSING_FACTORY, factory);

        return outMessage;
    }

    protected Service getService(AddressingHeaders headers, MessageContext context)
    {
        String serviceName = null;

        if (headers.getTo() != null)
        {
            int i = headers.getTo().lastIndexOf('/');
            serviceName = headers.getTo().substring(i + 1);
        }

        if (serviceName == null)
        {
            return null;
        }

        return context.getXFire().getServiceRegistry().getService(serviceName);
    }
}