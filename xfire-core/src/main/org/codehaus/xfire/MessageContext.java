package org.codehaus.xfire;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.exchange.InExchange;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.exchange.RobustInOutExchange;
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
    private static ThreadLocal messageContexts = new ThreadLocal();
	
    private Session session;
    private String serviceName;
    private String action;
    private Map properties;

    private Service service;

    private MessageExchange exchange;
    private InMessage inMessage;
    private OutMessage replyMessage;
    
    private Stack handlerStack;
    
    public MessageContext()
    {
        properties = new HashMap();
        messageContexts.set(this);
        handlerStack = new Stack();
    }

    /**
     * Create a MessageContext to invoke a service with the specified document as the request.
     */
    public MessageContext(String service,
                          String action,
                          Session session)
    {
        this();

        this.serviceName = service;
        this.action = action;
        this.session = session;
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
        
        if (mepUri.equals(SoapConstants.MEP_IN_OUT))
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
        return replyMessage;
    }

    public void setOutMessage(OutMessage replyDestination)
    {
        this.replyMessage = replyDestination;
    }

    public InMessage getInMessage()
    {
        return inMessage;
    }

    public void setInMessage(InMessage inMessage)
    {
        this.inMessage = inMessage;
    }

    /**
     * Get the message context for the current thread.
     *
     * @return The current MessageContext or null if there is none.
     */
    public static MessageContext getCurrentMessageContext()
    {
        return (MessageContext) messageContexts.get();
    }

    public Object getProperty(Object key)
    {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value)
    {
        properties.put(key, value);
    }

    public void setRequestStream(InputStream requestStream, String uri)
    {
        try
        {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(requestStream);
            
            setInMessage(new InMessage(xmlStreamReader, uri));
        }
        catch (XMLStreamException e)
        {
            throw new IllegalArgumentException("Invalid xml request stream: " + e);
        }
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

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String service)
    {
        this.serviceName = service;
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

    public Stack getHandlerStack()
    {
        return handlerStack;
    }

    public void setHandlerStack(Stack handlerStack)
    {
        this.handlerStack = handlerStack;
    }
    
}
