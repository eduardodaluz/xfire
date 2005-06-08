package org.codehaus.xfire.client;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.SoapServiceEndpoint;
import org.codehaus.xfire.transport.Transport;

public class Client
    extends SoapServiceEndpoint
{
    private Object[] response;
    private Transport transport;
    private Service service;
    private String url;
    private int timeout = 10*1000;
    private MessageContext context;
    
    public Client(Transport transport, Service service, String url)
    {
        this.transport = transport;
        this.service = service;
        this.url = url;
        
        service.getBinding().setClientModeOn(true);
    }

    public Object[] invoke(OperationInfo op, Object[] params) throws XFireFault
    {
        OutMessage msg = new OutMessage("targeturl");
        msg.setBody(params);
        msg.setUri(url);
        msg.setSerializer(service.getBinding());
        
        context = new MessageContext();
        context.setOutMessage(msg);
        context.setService(service);
        
        ClientInOutExchange exchange = new ClientInOutExchange(context, this);
        exchange.setOperation(op);
        context.setExchange(exchange);
        
        Channel channel = transport.createChannel("client");
        channel.open();
        channel.setEndpoint(this);
        channel.setService(service);
        channel.send(context, msg);
        
        int count = 0;
        while (response == null && count < timeout)
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
        
        channel.close();
        return response;
    }

    
    public void onReceive(MessageContext inContext, InMessage msg)
    {
        super.onReceive(context, msg);
    }

    public void receive(Object[] response)
    {
        this.response = response;
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
}