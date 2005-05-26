package org.codehaus.xfire.transport.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;

public class LocalTransport
    extends AbstractTransport
{
    private static final Log log = LogFactory.getLog(LocalTransport.class);
    
    public final static String NAME = "urn:xfire:transport:local";
    public final static String URI_PREFIX = NAME + ":";
   
    public String getName()
    {
        return NAME;
    }

    protected Channel createNewChannel(String uri, Service service)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        Channel c = new LocalChannel(uri, this);
        
        if (service != null)
        {
            c.setEndpoint(new ServiceEndpoint());
        }
        
        channels.put(uri, c);
        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }
}
