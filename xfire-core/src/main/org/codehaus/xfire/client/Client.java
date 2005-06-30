package org.codehaus.xfire.client;

import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.ObjectBinding;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.SoapServiceEndpoint;
import org.codehaus.xfire.transport.Transport;

public class Client
    extends SoapServiceEndpoint
{
    private Object[] response;
    private Transport transport;
    private Service service;
    private ObjectBinding clientBinding;
    private String url;
    private int timeout = 10*1000;
    private MessageContext context;
    private XFireFault fault;
    private String endpointUri;
    
    public Client(Transport transport, Service service, String url)
    {
        this(transport, service, url, null);
    }
    
    public Client(Transport transport, Service service, String url, String endpointUri)
    {
        this.transport = transport;
        this.service = new Service(service.getServiceInfo());
        this.url = url;
        this.endpointUri = endpointUri;
        
        clientBinding = (ObjectBinding) ((AbstractBinding) service.getBinding()).clone();
        clientBinding.setClientModeOn(true);
        this.service.setBinding(clientBinding);
        this.service.setFaultSerializer(service.getFaultSerializer());
        this.service.setSoapVersion(service.getSoapVersion());
    }

    public Object[] invoke(OperationInfo op, Object[] params) throws XFireException, XFireFault
    {
        OutMessage msg = new OutMessage("targeturl");
        msg.setBody(params);
        msg.setUri(url);
        msg.setSerializer(service.getBinding());
        msg.setAction(op.getAction());
        
        context = new MessageContext();
        context.setOutMessage(msg);
        context.setService(service);
        
        ClientInOutExchange exchange = new ClientInOutExchange(context, this);
        exchange.setOperation(op);
        context.setExchange(exchange);
        
        Channel channel = null;
        try
        {
            if (endpointUri == null)
                channel = transport.createChannel();
            else
                channel = transport.createChannel(endpointUri);
        }
        catch (Exception e)
        {
            throw new XFireException("Couldn't open channel.", e);
        }

        channel.setEndpoint(this);
        channel.setService(service);
        channel.send(context, msg);
        
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
            throw fault;
        
        return response;
    }

    
    public void onReceive(MessageContext inContext, InMessage msg)
    {
        super.onReceive(context, msg);
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
}