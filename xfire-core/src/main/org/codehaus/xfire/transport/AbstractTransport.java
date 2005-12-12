package org.codehaus.xfire.transport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.UID;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 21, 2004
 */
public abstract class AbstractTransport
    extends AbstractHandlerSupport
    implements Transport
{
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

    public Channel createChannel() throws Exception
    {
        return createChannel(getUriPrefix() + UID.generate());
    }

    public Channel createChannel(String uri) throws Exception
    {
        Channel c = (Channel) channels.get(uri);

        if (c == null)
        {
            c = createNewChannel(uri);

            channels.put(c.getUri(), c);

            c.open();
        }

        return c;
    }

    protected Map getChannelMap()
    {
        return channels;
    }
  
    public String[] getSupportedBindings()
    {
        return new String[0];
    }

    
    protected abstract Channel createNewChannel(String uri);
    protected abstract String getUriPrefix();
    protected abstract String[] getKnownUriSchemes();

    public boolean isUriSupported(String uri)
    {
        String[] schemes = getKnownUriSchemes();
        for (int i = 0; i < schemes.length; i++)
        {
            if (uri.startsWith(schemes[i])) return true;
        }
        
        return false;
    }

    public Binding findBinding(MessageContext context, Service service)
    {
        return service.getBinding(getSupportedBindings()[0]);
    }
}
