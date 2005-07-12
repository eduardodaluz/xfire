package org.codehaus.xfire.exchange;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.dead.DeadLetterTransport;

public abstract class AbstractMessageExchange
    implements MessageExchange
{
    private OperationInfo operation;

    private MessageContext context;
    
    private InMessage inMessage;
    private OutMessage outMessage;
    private AbstractMessage faultMessage;
    
    public AbstractMessageExchange(MessageContext context)
    {
        this.context = context;
    }
    
    public MessageContext getContext()
    {
        return context;
    }

    public OperationInfo getOperation()
    {
        return operation;
    }

    public void setOperation(OperationInfo operation)
    {
        this.operation = operation;
    }

    public InMessage getInMessage()
        throws UnsupportedOperationException
    {
        return inMessage;
    }

    public OutMessage getOutMessage()
        throws UnsupportedOperationException
    {
        return outMessage;
    }

    public AbstractMessage getFaultMessage()
        throws UnsupportedOperationException
    {
        return faultMessage;
    }

    public void setFaultMessage(AbstractMessage faultMessage)
    {
        this.faultMessage = faultMessage;
    }

    public void setInMessage(InMessage inMessage)
    {
        this.inMessage = inMessage;
    }

    public void setOutMessage(OutMessage outMessage)
    {
        this.outMessage = outMessage;
    }

    public AbstractMessage getMessage(String type)
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasFaultMessage()
    {
        return false;
    }

    public boolean hasInMessage()
    {
        return false;
    }

    public boolean hasMessage(String type)
    {
        return false;
    }

    public boolean hasOutMessage()
    {
        return false;
    }

    public void setMessage(String type, AbstractMessage faultMessage)
    {
        throw new UnsupportedOperationException();
    }

    public Channel getInChannel()
    {
        return getDeadLetterChannel();
    }

    public Channel getOutChannel()
    {
        return getDeadLetterChannel();
    }

    public Channel getFaultChannel()
    {
        return getDeadLetterChannel();
    }
    
    public Channel getDeadLetterChannel()
    {
        TransportManager tm = getContext().getXFire().getTransportManager();
        Transport transport = tm.getTransport(DeadLetterTransport.NAME);
        
        try
        {
            return transport.createChannel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return null;
        }
    }
}
