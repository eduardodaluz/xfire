package org.codehaus.xfire.client;

/**
 * Base ClientHandler class.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 1, 2004
 */
public abstract class AbstractClientHandler
    implements ClientHandler
{
    public boolean hasRequest()
    {
        return true;
    }
}
