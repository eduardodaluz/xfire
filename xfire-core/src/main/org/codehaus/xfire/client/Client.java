package org.codehaus.xfire.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.exchange.RobustInOutExchange;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.handler.OutMessageSender;
import org.codehaus.xfire.handler.ParseMessageHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectBinding;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;

public class Client
    extends AbstractHandlerSupport
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(Client.class);

    private Object[] response;
    private Transport transport;
    private Service service;
    private ObjectBinding clientBinding;
    private String url;
    private int timeout = 10*1000;
    private MessageContext context;
    private Exception fault;
    private String endpointUri;
    private List inPhases;
    private List outPhases;
   
    /** The XFire instance. This is only needed when invoking local services. */
    private XFire xfire;
    
    public Client(Transport transport, Service service, String url)
    {
        this(transport, service, url, null);
    }
    
    public Client(Transport transport, Service service, String url, String endpointUri)
    {
        this.transport = transport;
        this.url = url;
        this.endpointUri = endpointUri;
        
        clientBinding = (ObjectBinding) ((AbstractBinding) service.getBinding()).clone();
        clientBinding.setClientModeOn(true);
        
        // Create a service clone
        this.service = new Service(service.getServiceInfo());
        this.service.setBinding(clientBinding);
        this.service.setFaultSerializer(service.getFaultSerializer());
        this.service.setSoapVersion(service.getSoapVersion());
        
        inPhases = new ArrayList();
        outPhases = new ArrayList();
        createPhases();
        
        addOutHandler(new OutMessageSender());
        addInHandler(new ParseMessageHandler());
    }

    public Object[] invoke(OperationInfo op, Object[] params) throws Exception
    {
        try
        {
            OutMessage msg = new OutMessage("targeturl");
            msg.setBody(params);
            msg.setUri(url);
            msg.setSerializer(service.getBinding());
            msg.setAction(op.getAction());
            msg.setChannel(getOutChannel());
            
            context = new MessageContext();
            context.setService(service);
            context.setXFire(xfire);
            
            RobustInOutExchange exchange = new RobustInOutExchange(context);
            exchange.setOperation(op);
            exchange.setOutMessage(msg);
            context.setExchange(exchange);
            
            HandlerPipeline pipeline = new HandlerPipeline(outPhases);
            pipeline.addHandlers(getOutHandlers());
            pipeline.addHandlers(transport.getOutHandlers());
        
            pipeline.invoke(context);
        }
        catch (Exception e1)
        {
            throw XFireFault.createFault(e1);
        }
        
        /**
         * If this is an asynchronous channel, we'll need to sleep() and wait
         * for a response. Channels such as HTTP will have the response set
         * by the time we get to this point.
         */
        if (response == null && fault == null)
        {
            int count = 0;
            while (response == null && fault == null && count < timeout)
            {
                try
                {
                    Thread.sleep(50);
                    count += 50;
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }

        if (fault != null)
        {
            Exception localFault = fault;
            fault = null;
            throw localFault;
        }
        
        Object[] localResponse = response;
        response = null;
        return localResponse;
    }
    
    public void onReceive(MessageContext recvContext, InMessage msg)
    {
        if (log.isDebugEnabled()) log.debug("Received message to " + msg.getUri());
        
        if (context.getExchange() == null)
        {
            context.setExchange(new RobustInOutExchange(context));
        }
        
        RobustInOutExchange exchange = (RobustInOutExchange) context.getExchange();
        exchange.setInMessage(msg);
        
        HandlerPipeline pipeline = new HandlerPipeline(inPhases);
        pipeline.addHandlers(getInHandlers());
        pipeline.addHandlers(transport.getInHandlers());
        
        try
        {
            pipeline.invoke(context);
            
            finishReadingMessage(msg, context);
            
            response = ((List) msg.getBody()).toArray();
        }
        catch (Exception e1)
        {
            XFireFault fault = XFireFault.createFault(e1);
            pipeline.handleFault(fault, context);
            this.fault = fault;
            
            Element detail = fault.getDetail();
            if (detail != null)
            {
                processFaultDetail(detail);
            }
        }
    }
    
    protected void processFaultDetail(Element detail)
    {
        if (detail.getContentSize() > 0)
        {
            Element exDetail = (Element) detail.getContent().get(0);
            
            MessagePartInfo faultPart = getFaultPart(context.getExchange().getOperation(),
                                                     exDetail);

            if (faultPart == null)
                return;
            
            try
            {
                BindingProvider provider = context.getService().getBinding()
                        .getBindingProvider();
                JDOMStreamReader reader = new JDOMStreamReader(exDetail);
                reader.nextTag();
                
                this.fault = (Exception) provider.readParameter(faultPart, reader, context);
            }
            catch (XFireFault e)
            {
                this.fault = e;
            }
            catch (XMLStreamException e)
            {
                this.fault = e;
            }
        }
    }
    
    protected MessagePartInfo getFaultPart(OperationInfo operation, Element exDetail)
    {
        QName qname = new QName(exDetail.getNamespaceURI(), exDetail.getName());
        
        for (Iterator itr = operation.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            MessagePartInfo part = faultInfo.getMessagePart(qname);
            
            if (part != null) return part;
        }
        
        return null;
    }

    public void finishReadingMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = message.getXMLStreamReader();

        try
        {
            while (reader.hasNext()) reader.next();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse message.", e, XFireFault.SENDER);
        }
    }
    
    protected void createPhases()
    {
        inPhases = new ArrayList();
        inPhases.add(new Phase(Phase.TRANSPORT, 1000));
        inPhases.add(new Phase(Phase.PARSE, 2000));
        inPhases.add(new Phase(Phase.PRE_DISPATCH, 3000));
        inPhases.add(new Phase(Phase.DISPATCH, 4000));
        inPhases.add(new Phase(Phase.POLICY, 5000));
        inPhases.add(new Phase(Phase.USER, 6000));
        inPhases.add(new Phase(Phase.PRE_INVOKE, 7000));
        inPhases.add(new Phase(Phase.SERVICE, 8000));

        outPhases = new ArrayList();
        outPhases.add(new Phase(Phase.POST_INVOKE, 1000));
        outPhases.add(new Phase(Phase.POLICY, 2000));
        outPhases.add(new Phase(Phase.USER, 3000));
        outPhases.add(new Phase(Phase.TRANSPORT, 4000));
        outPhases.add(new Phase(Phase.SEND, 5000));
    }

    public Channel getOutChannel()
    {
        Channel channel = null;
        try
        {
            String uri = getEndpointUri();
            if (uri == null)
                channel = getTransport().createChannel();
            else
                channel = getTransport().createChannel(uri);
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't open channel.", e);
        }

        channel.setEndpoint(this);
        
        return channel;
    }
    
    public Transport getTransport()
    {
        return transport;
    }

    public void receive(Object response)
    {
        this.response = ((List) response).toArray();
    }
    
    public Service getService()
    {
        return service;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getEndpointUri()
    {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri)
    {
        this.endpointUri = endpointUri;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public void receiveFault(XFireFault fault)
    {
        this.fault = fault;
    }

    public XFire getXFire()
    {
        return xfire;
    }

    public void setXFire(XFire xfire)
    {
        this.xfire = xfire;
    }
}