package org.codehaus.xfire.attachments;

import javax.activation.DataHandler;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SimpleAttachment
	implements Attachment
{
    private DataHandler handler;
    private	String id;
    
    public SimpleAttachment(String id, DataHandler handler)
    {
        this.id = id;
        this.handler = handler;
    }

    public String getId()
    {
        return id;
    }

    public DataHandler getDataHandler()
    {
        return handler;
    }
}
