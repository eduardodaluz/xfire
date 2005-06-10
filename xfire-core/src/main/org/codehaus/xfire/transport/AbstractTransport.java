package org.codehaus.xfire.transport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.UID;

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

    private Map/*<String uri,Channel c>*/ channels = new HashMap();

    /**
     * Disposes all the existing channels.
     */
    public void dispose()
    {
        for (Iterator itr = channels.values().iterator(); itr.hasNext();)
        {
            Channel channel = (Channel) itr.next();
            channel.close();
        }
    }

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

    public Channel createChannel() throws Exception
    {
        return createChannel(getUriPrefix() + UID.generate());
    }

    public Channel createChannel(String uri) throws Exception
    {
        Channel c = (Channel) channels.get(uri);

        if (c == null)
        {
            c = createNewChannel(uri, null);

            channels.put(c.getUri(), c);

            c.open();
        }

        return c;
    }

    public Channel createChannel(Service service) throws Exception
    {
        String uri = getUriPrefix() + service.getName();

        Channel c = (Channel) channels.get(uri);

        if (c == null)
        {
            c = createNewChannel(uri, service);

            channels.put(c.getUri(), c);

            c.open();
        }

        return c;
    }

    protected Map getChannelMap()
    {
        return channels;
    }

    protected abstract Channel createNewChannel(String uri, Service service);
    protected abstract String getUriPrefix();
}
