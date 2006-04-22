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
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ServiceInvocationHandler;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap11Binding;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.Soap12Binding;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;
import org.xml.sax.InputSource;

/**
 * A SOAP Client. This client can function in two modes.
 * <p>
 * The first is dynamic mode. In this mode the WSDL is retrieved for a service,
 * a {@link org.codehaus.xfire.service.Service} model is created from it, and 
 * it is used as metadata for the service.
 * @author Dan
 */
public class Client
    extends AbstractHandlerSupport
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(Client.class);

    /**
     * This is a variable set on the MessageContext to let particular Handlers
     * know that the invocation is a client invocation.
     */
    public static final String CLIENT_MODE = "client.mode";

    private Object[] response;
    private Channel channel;
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
    
    /**
     * Creates a client for a particular {@link Endpoint} on a specified {@link Transport}. The
     * client will create an anonymous Channel to talk to the service. The URI from the endpoint 
     * will be used as the destination for messages. 
     * @param t The Transport to use.
     * @param endpoint The Endpoint to invoke.
     */
    public Client(Transport t, Endpoint endpoint)
    {
        this(endpoint.getBinding(), t, endpoint.getBinding().getService(), endpoint.getUrl(), null);
    }

    /**
     * Create a client which uses a particular {@link Binding} with a specified URL. The
     * {@link Transport} is looked up via the {@link TransportManager} from its URL.
     * @param binding
     * @param url
     */
    public Client( Binding binding, String url)
    {
        this(binding, 
             XFireFactory.newInstance().getXFire().getTransportManager().getTransport(binding.getBindingId()), 
             binding.getService(), url, null);
    }
    
    /**
     * Create a client which uses a particular {@link Binding} with a specified URL
     * and a specified {@link Transport}.
     * @param transport The Transport to use.
     * @param binding
     * @param url
     */
    public Client(Transport t, Binding binding, String url)
    {
        this(binding, t, binding.getService(), url, null);
    }
    
    /**
     * Create a Client on the specified {@link Transport} for a {@link Service}.
     * The Client will look for an appropriate binding on the client bye attempting
     * to find the first Binding that is compatabile with the specified Transport.
     * 
     * @param transport
     * @param service
     * @param url The destination URL.
     */
    public Client(Transport transport, Service service, String url)
    {
        this(transport, service, url, null);
    }
    
    /**
     * Create a Client on the specified {@link Transport} for a {@link Service}.
     * The Client will look for an appropriate binding on the client bye attempting
     * to find the first Binding that is compatabile with the specified Transport.
     * 
     * @param transport
     * @param service The Service model which defines our operations.
     * @param url The destination URL.
     * @param endpointUri The URI to bind to on the client side. The client will look
     * for messages here.
     */
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
    
    /**
     * Create a Client on the specified {@link Transport} for a {@link Service}.
     * The Client will look for an appropriate binding on the client bye attempting
     * to find the first Binding that is compatabile with the specified Transport.
     * 
     * @param binding The Binding to use.
     * @param transport The Transport to send message through.
     * @param service The Service model which defines our operations.
     * @param url The destination URL.
     * @param endpointUri The URI to bind to on the client side. The client will look
     * for messages here.
     */
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


    /**
     * Creates a Client form a WSDL and a service class. The Client will attempt to use
     * the SOAP 1.1 HTTP binding.
     * 
     * @param definition The WSDL definition.
     * @param serviceClass The service class being used.
     * @throws Exception
     */
    public Client(Definition definition, Class serviceClass) throws Exception
    {
    	this();
        
		initFromDefinition(SoapHttpTransport.SOAP11_HTTP_BINDING, definition, serviceClass);
    }
    
    /**
     * Creates a Client form a WSDL, a service class and the specified binding id.
     * 
     * @param definition The WSDL definition.
     * @param serviceClass The service class being used.
     * @throws Exception
     */
    public Client(String binding, Definition definition, Class serviceClass) throws Exception
    {
    	this();
        
    	initFromDefinition(binding, definition, serviceClass);
    }
    
    /**
     * Creates a Client form a WSDL and a service class.
     * @param is The InputStream for the wsdl.
     * @param serviceClass The service class being used.
     * @throws Exception
     */
    public Client(InputStream is, Class serviceClass) throws Exception
    {
        this();
        
        try 
        {
            InputSource src = new InputSource(is);
            Definition def = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, src);
            initFromDefinition(SoapHttpTransport.SOAP11_HTTP_BINDING, def, serviceClass);
        }
        finally
        {
            is.close();
        }
    }
    
    public Client(URL wsdlLocation) throws Exception
    {
    	this(wsdlLocation.openStream(), null); 
    }
    
    public Client(URL wsdlLocation, Class serviceClass) throws Exception
    {
        this(wsdlLocation.openStream(), serviceClass);
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
        builder.build();
        
        Endpoint ep = findEndpoint(binding, builder.getAllServices());
        
        this.url = ep.getUrl();
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
            
            // TODO this should probably be in a seperate handler.
            // We'll have to address this when we add REST support.
            if (binding instanceof Soap11Binding)
                msg.setSoapVersion(Soap11.getInstance());
            else if (binding instanceof Soap12Binding)
                msg.setSoapVersion(Soap12.getInstance());
            
            context = new MessageContext();
            context.setService(service);
            context.setXFire(xfire);
            context.setBinding(binding);
            context.setProperty(CLIENT_MODE, Boolean.TRUE);
            context.setClient(this);

            MessageExchange exchange = new MessageExchange(context);
            exchange.setOperation(op);
            exchange.setOutMessage(msg);
            context.setCurrentMessage(msg);
            
            HandlerPipeline outPipe = new HandlerPipeline(xfire.getOutPhases());
            outPipe.addHandlers(xfire.getOutHandlers());
            outPipe.addHandlers(getOutHandlers());
            outPipe.addHandlers(transport.getOutHandlers());
            
            context.setOutPipeline(outPipe);

            ServiceInvocationHandler.writeHeaders(context);
            
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
        
        //getTransport().close(getOutChannel());
        
        return localResponse;
    }
    
    public Object[] invoke(String name, Object[] params) throws Exception
    {
        OperationInfo op = service.getServiceInfo().getOperation(name);
        if (op == null)
            throw new XFireRuntimeException("Could not find operation with name " + name);
        
        return invoke(op, params);
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
        
        if (!exchange.getOperation().hasOutput()) return;
        
        exchange.setInMessage(msg);
        context.setCurrentMessage(msg);
        
        try
        {
            HandlerPipeline inPipe = new HandlerPipeline(xfire.getInPhases());
            inPipe.addHandlers(getInHandlers());
            inPipe.addHandlers(transport.getInHandlers());
            recvContext.setInPipeline(inPipe);
            
            inPipe.invoke(context);
            
            MessageInfo msgInfo = exchange.getOperation().getOutputMessage();
            ServiceInvocationHandler.readHeaders(context, 
                                                 binding.getHeaders(msgInfo), 
                                                 (Object[]) context.getOutMessage().getBody());
            
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
        finally
        {
            if (msg.getAttachments() != null)
                msg.getAttachments().dispose();
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
        if (channel != null) return channel;
        
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
    
    public void close() 
    {
        if (channel != null)
            channel.close();
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

    public XFire getXFire()
    {
        return xfire;
    }

    public void setXFire(XFire xfire)
    {
        this.xfire = xfire;
    }
}