package org.codehaus.xfire;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.session.Session;

/**
 * Holds the request and response messages as well the session.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class MessageContext
{
    private static ThreadLocal messageContexts = new ThreadLocal();
    
    private Session session;
    private OutputStream responseStream;
    private String requestUri;
    private String serviceName;
    private String action;
    private Map properties;
    private String soapVersion;
    private Service service;
    
    /**
     * Create a MessageContext to invoke a service with the
     * specified document as the request.
     * 
     * @param serviceName The name of the service being invoked.
     * @param document The request document.
     */
    public MessageContext( String service,
                           String action,
                           OutputStream outputStream, 
                           Session session, 
                           String requestUri )
    {
        messageContexts.set(this);
        
        properties = new HashMap();
        
        this.serviceName = service;
        this.action = action;
        this.responseStream = outputStream;
        this.session = session;
        this.requestUri = requestUri;
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

    public Object getProperty( Object key )
    {
    	return properties.get(key);
    }
    
    public void setProperty( Object key, Object value )
    {
    	properties.put(key, value);
    }
    
    public String getRequestUri()
    {
        return requestUri;
    }

    public void setRequestUri( String requestUri )
    {
        this.requestUri = requestUri;
    }
    
    public OutputStream getResponseStream()
    {
        return responseStream;
    }
    
    public void setResponseStream( OutputStream responseStream )
    {
        this.responseStream = responseStream;
    }
    
    public Session getSession()
    {
        return session;
    }
   
    public void setSession( Session session )
    {
        this.session = session;
    }
    
    public String getAction()
    {
        return action;
    }
    
    public void setAction( String action )
    {
        this.action = action;
    }
    
    public String getServiceName()
    {
        return serviceName;
    }
    
    public void setServiceName( String service )
    {
        this.serviceName = service;
    }

    public String getSoapVersion()
    {
        return soapVersion;
    }
    
    public void setSoapVersion( String soapVersion )
    {
        this.soapVersion = soapVersion;
    }
    
    public Service getService()
    {
        return service;
    }
    
    public void setService( Service service )
    {
        this.service = service;
    }
}
