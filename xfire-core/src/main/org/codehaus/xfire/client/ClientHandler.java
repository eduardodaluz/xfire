package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * <p>
 * A ClientHandler creates the necessary requestion and response objects
 * from the XML streams.  If this is a SOAP invocation, there will be a
 * handler for the header and for the body.
 * </p>
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public interface ClientHandler
{
    XMLStreamReader createRequest() throws XMLStreamException;
    
    void handleResponse( XMLStreamReader reader ) throws XMLStreamException;
}