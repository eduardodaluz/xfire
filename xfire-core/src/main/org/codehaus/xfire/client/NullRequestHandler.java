package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public abstract class NullRequestHandler 
    implements ClientHandler
{
    public void writeRequest(XMLStreamWriter writer) 
        throws XMLStreamException
    {
    }
}
