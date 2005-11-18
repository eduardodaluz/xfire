package org.codehaus.xfire.transport.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;

/**
 * A transport which just writes messages to stdout
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class LogTransport extends AbstractTransport
{
    private static final Log log = LogFactory.getLog( LogTransport.class );

    public final static String BINDING_ID = "urn:xfire:transport:log";
    public final static String URI_PREFIX = "xfire.log://";

    protected Channel createNewChannel( String uri )
    {
        log.debug( "Creating new channel for uri: " + uri );

        LogChannel c = new LogChannel( uri, this );
        c.setEndpoint( new DefaultEndpoint() );

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    public String[] getSupportedBindings()
    {
        return new String[]{ BINDING_ID };
    }

    public String[] getKnownUriSchemes()
    {
        return new String[]{ URI_PREFIX };
    }
}