package org.codehaus.xfire.attachments;

import java.io.IOException;

import javax.activation.DataHandler;

/**
 * An attachment from a SOAP invocation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Attachment
{
    public DataHandler getDataHandler();

    /**
     * @return The attachment id.
     */
    public String getId();
}
