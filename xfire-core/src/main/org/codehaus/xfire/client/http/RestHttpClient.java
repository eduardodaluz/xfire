package org.codehaus.xfire.client.http;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.util.STAXUtils;

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
        XMLStreamReader reader = handler.createRequest();
        if ( reader != null )
        {
            writer.writeStartDocument();
            STAXUtils.copy(reader, writer);
            writer.writeEndDocument();
        }

        writer.close();
    }

    protected void readResponse(XMLStreamReader reader) 
        throws XMLStreamException
    {
        handler.handleResponse(reader);
    } 
}
