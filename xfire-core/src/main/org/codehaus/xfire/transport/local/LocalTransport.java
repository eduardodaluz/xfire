package org.codehaus.xfire.transport.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;

/**
 * A transport which passes messages via the JVM.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class LocalTransport
    extends AbstractTransport
{
    private static final Log log = LogFactory.getLog(LocalTransport.class);
    
    public final static String NAME = "urn:xfire:transport:local";
    public final static String URI_PREFIX = "xfire.local://";
    
    public String getName()
    {
        return NAME;
    }

    protected Channel createNewChannel(String uri)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        LocalChannel c = new LocalChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    public String[] getKnownUriSchemes()
    {
        return new String[] { URI_PREFIX };
    }
}
