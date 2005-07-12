package org.codehaus.xfire;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.exchange.InExchange;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.exchange.RobustInOutExchange;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Session;

/**
 * Holds inforrmation about the message request and response.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class MessageContext
{
    private Session session;
    private Map properties;

    private Service service;

    private MessageExchange exchange;

    private HandlerPipeline inPipeline;
    private HandlerPipeline outPipeline;
    private XFire xfire;
    
    public MessageContext()
    {
        properties = new HashMap();
    }
    
    public XFire getXFire()
    {
        return xfire;
    }

    public void setXFire(XFire xfire)
    {
        this.xfire = xfire;
    }

    public MessageExchange createMessageExchange(OperationInfo operation)
    {
        MessageExchange ex = createMessageExchange(operation.getMEP());
        ex.setOperation(operation);
        
        return ex;
    }

    public MessageExchange createMessageExchange(String mepUri)
    {
        MessageExchange ex = null;
        
        if (mepUri.equals(SoapConstants.MEP_ROBUST_IN_OUT))
        {
            ex = new RobustInOutExchange(this);
        }
        else if (mepUri.equals(SoapConstants.MEP_IN))
        {
            ex = new InExchange(this);
        }
        
        setExchange(ex);
        
        return ex;
    }
    
    public MessageExchange getExchange()
    {
        return exchange;
    }

    public void setExchange(MessageExchange exchange)
    {
        this.exchange = exchange;
    }

    public OutMessage getOutMessage()
    {
        return exchange.getOutMessage();
    }

    public InMessage getInMessage()
    {
        return exchange.getInMessage();
    }

    public Object getProperty(Object key)
    {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value)
    {
        properties.put(key, value);
    }

    /**
     * The session that this request is a part of.
     *
     * @return
     */
    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    /**
     * The service being invoked.
     *
     * @return
     */
    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public HandlerPipeline getInPipeline()
    {
        return inPipeline;
    }

    public void setInPipeline(HandlerPipeline messagePipeline)
    {
        this.inPipeline = messagePipeline;
    }

    public HandlerPipeline getOutPipeline()
    {
        return outPipeline;
    }

    public void setOutPipeline(HandlerPipeline outPipeline)
    {
        this.outPipeline = outPipeline;
    }
}
