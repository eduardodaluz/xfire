package org.codehaus.xfire.client;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.handler.OutMessageSender;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;
import org.xml.sax.InputSource;

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
        addFaultHandler(new ClientFaultConverter());
    }
    
    public Client(Transport t, Endpoint endpoint)
    {
        this(endpoint.getBinding(), t, endpoint.getBinding().getService(), endpoint.getAddress(), null);
    }

    public Client( Binding binding, String url)
    {
        this(binding, 
             XFireFactory.newInstance().getXFire().getTransportManager().getTransport(binding.getBindingId()), 
             binding.getService(), url, null);
    }
    
    public Client(Transport t, Binding binding, String url)
    {
        this(binding, t, binding.getService(), url, null);
    }
    
    public Client(Transport transport, Service service, String url)
    {
        this(transport, service, url, null);
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
    
    public Client(Binding binding, Transport transport, Service service, String url, String endpointUri)
    {
        this();
        this.transport = transport;
        this.url = url;
        this.endpointUri = endpointUri;

        // Create a service clone
        setService(service);

        this.binding = binding;
    }

    public Client(Definition definition, Class serviceClass) throws Exception
    {
    	this();
        
		Transport transport = xfire.getTransportManager().getTransportForUri(SoapHttpTransport.SOAP11_HTTP_BINDING);
    	initFromDefinition(SoapHttpTransport.SOAP11_HTTP_BINDING, definition, serviceClass);
    }
    
    /*public Client(Transport transport, Definition definition, Class serviceClass) throws Exception
    {
    	this();
        
    	initFromDefinition(transport, definition, serviceClass);
    }*/
    
    public Client(URL wsdlLocation) throws Exception
    {
    	this(wsdlLocation, null);
    }
    
    public Client(URL wsdlLocation, Class serviceClass) throws Exception
    {
    	this();
        
    	InputStream is = wsdlLocation.openStream();
    	try 
    	{
    		InputSource src = new InputSource(is);
    		Definition def = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, src);
    		Transport transport = xfire.getTransportManager().getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);
    		initFromDefinition(SoapHttpTransport.SOAP11_HTTP_BINDING, def, serviceClass);
    	}
    	finally
    	{
    		is.close();
    	}
    }
    
    private void setService(Service service)
    {
        this.service = service;
        this.service.setFaultSerializer(service.getFaultSerializer());
    }
    
    protected void initFromDefinition(String binding, Definition definition, Class serviceClass) throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(definition);
        builder.setTransportManager(xfire.getTransportManager());
        builder.walkTree();
        
        Endpoint ep = findEndpoint(binding, builder.getServices());
        
        this.url = ep.getAddress();
        this.binding = ep.getBinding();
        this.transport = getXFire().getTransportManager().getTransport(binding);
        
        if (serviceClass != null)
        {
            ep.getBinding().getService().getServiceInfo().setServiceClass(serviceClass);
        }
        
        setService(ep.getBinding().getService());
    }
    
    public Endpoint findEndpoint(String binding, Collection services)
    {
        for (Iterator itr = services.iterator(); itr.hasNext();)
        {
            Service service = (Service) itr.next();
            
            for (Iterator eitr = service.getEndpoints().iterator(); eitr.hasNext();)
            {
                Endpoint ep = (Endpoint) eitr.next();
                
                if (ep.getBinding().getBindingId().equals(binding))
                {
                    return ep;
                }
            }
        }
        return null;
    }

    private Binding findBinding(Transport transport, Service service)
    {
        String[] ids = transport.getSupportedBindings();
        for (int i = 0; i < ids.length; i++)
        {
            Binding b = service.getBinding(ids[i]);
            if (b != null)
            {
                return b;
            }
        }

        return findSoapBinding(service);
        // throw new IllegalStateException("Couldn't find an appropriate binding for the selected transport.");
    }

    private AbstractSoapBinding findSoapBinding(Service service)
    {
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Object o = itr.next();
            if (o instanceof AbstractSoapBinding)
            {
                return (AbstractSoapBinding) o;
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
            context.setClient(this);
            
            MessageExchange exchange = new MessageExchange(context);
            exchange.setOperation(op);
            exchange.setOutMessage(msg);
            
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
        
        waitForResponse();

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
    
    public Object[] invoke(String name, Object[] params) throws Exception
    {
        return invoke(service.getServiceInfo().getOperation(name), params);
    }

    /**
     * Waits for a response from the service.
     */
    protected void waitForResponse()
    {
        /**
         * If this is an asynchronous channel, we'll need to sleep() and wait
         * for a response. Channels such as HTTP will have the response set
         * by the time we get to this point.
         */
        if (!getOutChannel().isAsync() || 
                response != null && 
                fault != null && 
                !context.getExchange().getOperation().hasOutput())
        {
            return;
        }
        
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
    
    public void onReceive(MessageContext recvContext, InMessage msg)
    {
        if (log.isDebugEnabled()) log.debug("Received message to " + msg.getUri());
        
        if (context.getExchange() == null)
        {
            new MessageExchange(context);
        }
        
        MessageExchange exchange = context.getExchange();
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
            
            AbstractMessage faultMsg = context.getExchange().getFaultMessage();
            faultMsg.setBody(fault);
            
            HandlerPipeline inPipe = new HandlerPipeline(xfire.getFaultPhases());
            inPipe.addHandlers(getFaultHandlers());
            inPipe.addHandlers(transport.getFaultHandlers());

            try
            {
                inPipe.invoke(context);
                
                this.fault = (Exception) faultMsg.getBody();
            }
            catch (Exception e)
            {
                this.fault = e;
            }
        }
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