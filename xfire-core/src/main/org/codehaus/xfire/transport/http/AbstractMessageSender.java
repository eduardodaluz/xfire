package org.codehaus.xfire.transport.http;

import java.io.IOException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;

public abstract class AbstractMessageSender
{
    private OutMessage message;
    private MessageContext context;
    
    public AbstractMessageSender(OutMessage message, MessageContext context)
    {
        this.message = message;
        this.context = context;
    }
    
    public abstract void open() throws IOException, XFireFault;
    public abstract void send() throws IOException, XFireFault;
    public abstract void close() throws XFireException;

    public abstract InMessage getInMessage() throws IOException;
    
    public MessageContext getMessageContext()
    {
        return context;
    }

    public void setMessageContext(MessageContext context)
    {
        this.context = context;
    }

    public OutMessage getMessage()
    {
        return message;
    }

    public void setMessage(OutMessage message)
    {
        this.message = message;
    }

    public String getEncoding()
    {
        return message.getEncoding();
    }

    public String getSoapAction()
    {
        return message.getAction();
    }
    
    public String getUri()
    {
        return message.getUri();
    }

}   
