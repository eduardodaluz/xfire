package org.codehaus.xfire.client;

import org.codehaus.xfire.attachments.Attachments;

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
    
    public Attachments getAttachments()
    {
        return null;
    }
}
