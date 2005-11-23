package org.codehaus.xfire.transport;

import org.codehaus.xfire.handler.HandlerSupport;

/**
 * Transport
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Transport
    extends ChannelFactory, HandlerSupport
{
    boolean isUriSupported(String uri);
    
    String[] getSupportedBindings();
    
    void dispose();
}
