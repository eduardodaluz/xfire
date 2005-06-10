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
    /**
     * Create a channel with a new unique URI.
     * 
     * @return The channel.
     * @throws Exception Occurs if there was an exception creating or opening the channel.
     */
    Channel createChannel() throws Exception;
    
    /**
     * Create a channel with a specified URI.
     * 
     * @param uri The uri for the channel.
     * @return The channel.
     * @throws Exception Occurs if there was an exception creating or opening the channel.
     */
    Channel createChannel(String uri) throws Exception;
    
    /**
     * Create a channel for a service. A uri is contructed automatically, possibly using
     * the service name.
     * 
     * @param endpoint The service.
     * @return The channel.
     * @throws Exception Occurs if there was an exception creating or opening the channel.
     */
    Channel createChannel(Service endpoint) throws Exception;
}
