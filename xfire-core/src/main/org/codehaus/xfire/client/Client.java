package org.codehaus.xfire.client;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.exchange.RobustInOutExchange;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.handler.OutMessageSender;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;
import org.jdom.Element;

public class Client
    extends AbstractHandlerSupport
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(Client.class);

    public static final String CLIENT_MODE = "client.mode";

    private Object[] response;
    private Transport transport;
    private Service service;
    private Binding binding;
    private String url;
    private int timeout = 10*1000;
    private MessageContext context;
    private Exception fault;
    private String endpointUri;
   
    /** The XFire instance. This is only needed when invoking local services. */
    private XFire xfire = XFireFactory.newInstance().getXFire();
    
    protected Client()
    {
        addOutHandler(new OutMessageSender());
    }
    
    public Client(Endpoint endpoint)
    {
        this(endpoint.getBinding(), endpoint.getAddress());
    }
    
    public Client(Binding binding, String url)
    {
        this(binding.getTransport(), binding.getService(), url);
        this.binding = binding;
    }
    
    public Client(Transport transport, Service service, String url)
    {
        this(transport, service, url, null);
        
        findBinding(transport, service);       
    }
    
    public Client(Transport transport, Service service, String url, String endpointUri)
    {
        this();
        this.transport = transport;
        this.url = url;
        this.endpointUri = endpointUri;

        // Create a service clone
        setService(service);

        this.binding = findBinding(transport, service);
    }

    private void setService(Service service)
    {
        this.service = service;
        this.service.setFaultSerializer(service.getFaultSerializer());
        this.service.setSoapVersion(service.getSoapVersion());
    }
    
    public Client(URL wsdlLocation) throws Exception
    {
        this();
        WSDLServiceBuilder builder = new WSDLServiceBuilder(wsdlLocation.openStream());
        builder.setTransportManager(xfire.getTransportManager());
        builder.walkTree();
        
        Endpoint ep = findEndpoint(builder.getServices());
        
        this.url = ep.getAddress();
        this.binding = ep.getBinding();
        this.transport = ep.getBinding().getTransport();
        setService(ep.getBinding().getService());
    }
    
    public Endpoint findEndpoint(Collection services)
    {
        for (Iterator itr = services.iterator(); itr.hasNext();)
        {
            Service service = (Service) itr.next();
            
            for (Iterator eitr = service.getEndpoints().iterator(); eitr.hasNext();)
            {
                Endpoint ep = (Endpoint) eitr.next();
                
                if (ep.getBinding().getTransport() instanceof SoapHttpTransport)
                {
                    return ep;
                }
            }
        }
        return null;
    }

    private Binding findBinding(Transport transport, Service service)
    {
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Binding b = (Binding) itr.next();
            if (b.getTransport() != null && b.getTransport().equals(transport))
            {
                return b;
            }
        }

        return findSoapBinding(service);
        // throw new IllegalStateException("Couldn't find an appropriate binding for the selected transport.");
    }

    private SoapBinding findSoapBinding(Service service)
    {
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Object o = itr.next();
            if (o instanceof SoapBinding)
            {
                return (SoapBinding) o;
            }
        }
        return null;
    }

    public Object[] invoke(OperationInfo op, Object[] params) throws Exception
    {
        try
        {
            OutMessage msg = new OutMessage(url);
            msg.setBody(params);
            msg.setChannel(getOutChannel());
            
            context = new MessageContext();
            context.setService(service);
            context.setXFire(xfire);
            context.setBinding(binding);
            context.setProperty(CLIENT_MODE, Boolean.TRUE);
            
            RobustInOutExchange exchange = new RobustInOutExchange(context);
            exchange.setOperation(op);
            exchange.setOutMessage(msg);
            context.setExchange(exchange);
            
            HandlerPipeline outPipe = new HandlerPipeline(xfire.getOutPhases());
            outPipe.addHandlers(getOutHandlers());
            outPipe.addHandlers(transport.getOutHandlers());
            context.setOutPipeline(outPipe);

            outPipe.invoke(context);
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

        try
        {
            HandlerPipeline inPipe = new HandlerPipeline(xfire.getInPhases());
            inPipe.addHandlers(getInHandlers());
            inPipe.addHandlers(transport.getInHandlers());
            recvContext.setInPipeline(inPipe);
            
            inPipe.invoke(context);
            
            finishReadingMessage(msg, context);
            
            response = ((List) msg.getBody()).toArray();
        }
        catch (Exception e1)
        {
            XFireFault fault = XFireFault.createFault(e1);
            recvContext.getInPipeline().handleFault(fault, context);
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
                BindingProvider provider = context.getService().getBindingProvider();
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

    public void setTransport(Transport transport)
    {
        this.transport = transport;
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