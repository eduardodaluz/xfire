package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public abstract class NullRequestHandler 
    implements ClientHandler
{
    public boolean hasRequest()
    {
        return false;
    }

    public void writeRequest(XMLStreamWriter writer) 
        throws XMLStreamException
    {
    }
    
    public Attachments getAttachments()
    {
        return null;
    }
}
