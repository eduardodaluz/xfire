package org.codehaus.xfire.transport;

import org.codehaus.xfire.handler.HandlerPipeline;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 21, 2004
 */
public abstract class AbstractTransport
    implements Transport
{
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private HandlerPipeline faultPipeline;
    
    
    /**
     * @return Returns the faultPipeline.
     */
    public HandlerPipeline getFaultPipeline()
    {
        return faultPipeline;
    }
    
    /**
     * @param faultPipeline The faultPipeline to set.
     */
    public void setFaultPipeline(HandlerPipeline faultPipeline)
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
}
