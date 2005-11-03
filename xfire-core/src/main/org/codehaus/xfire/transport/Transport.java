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
    String getName();

    String[] getKnownUriSchemes();
    
    String[] getSupportedBindings();
    
    void dispose();
}
