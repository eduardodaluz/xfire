package org.codehaus.xfire.client.http;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.ClientHandler;

/**
 * A client which invokes REST style services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class RestHttpClient 
    extends AbstractHttpClient
{
    private ClientHandler handler;
    
    public RestHttpClient(ClientHandler bodyHandler, String url)
    {
        super();
        this.handler = bodyHandler;
        setUrl( url );
    }    
    
    protected void writeRequest(XMLStreamWriter writer) 
        throws XMLStreamException
    {
        writer.writeStartDocument();
        handler.writeRequest(writer);
        writer.writeEndDocument();

        writer.close();
    }

    protected void readResponse(XMLStreamReader reader) 
        throws XMLStreamException
    {
        handler.handleResponse(reader);
    } 
}
