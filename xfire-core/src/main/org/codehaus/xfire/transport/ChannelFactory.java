package org.codehaus.xfire.transport;

import org.codehaus.xfire.service.Service;

/**
 * Creates channels. Transports implement this interface.
 * 
 * @see org.codehaus.xfire.transport.Transport
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ChannelFactory
{
    Channel createChannel(String uri);
    
    Channel createChannel(Service endpoint);
}
