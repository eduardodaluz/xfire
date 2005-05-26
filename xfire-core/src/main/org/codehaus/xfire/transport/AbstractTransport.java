package org.codehaus.xfire.transport;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 21, 2004
 */
public abstract class AbstractTransport
    implements Transport
{
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;
    
    public Map/*<String uri,Channel c>*/ channels = new HashMap();
 
    /**
     * @return Returns the faultPipeline.
     */
    public FaultHandlerPipeline getFaultPipeline()
    {
        return faultPipeline;
    }
    
    /**
     * @param faultPipeline The faultPipeline to set.
     */
    public void setFaultPipeline(FaultHandlerPipeline faultPipeline)
    {
        this.faultPipeline = faultPipeline;
    }
    
    /**
     * @return Returns the requestPipeline.
     */
    public HandlerPipeline getRequestPipeline()
    {
        return requestPipeline;
    }
    
    /**
     * @param requestPipeline The requestPipeline to set.
     */
    public void setRequestPipeline(HandlerPipeline requestPipeline)
    {
        this.requestPipeline = requestPipeline;
    }
    
    /**
     * @return Returns the responsePipeline.
     */
    public HandlerPipeline getResponsePipeline()
    {
        return responsePipeline;
    }
    
    /**
     * @param responsePipeline The responsePipeline to set.
     */
    public void setResponsePipeline(HandlerPipeline responsePipeline)
    {
        this.responsePipeline = responsePipeline;
    }

    public Channel createChannel(String uri)
    {
        Channel c = (Channel) channels.get(uri);
        
        if (c == null)
        {
            c = createNewChannel(uri, null);
        }
        
        return c;
    }

    public Channel createChannel(Service service)
    {
        String uri = getUriPrefix() + service.getName();
        
        Channel c = (Channel) channels.get(uri);
        
        if (c == null)
        {
            c = createNewChannel(uri, service);
        }
        
        return c;
    }

    protected abstract Channel createNewChannel(String uri, Service service);
    protected abstract String getUriPrefix();
}
