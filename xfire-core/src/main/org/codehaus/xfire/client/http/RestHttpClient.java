package org.codehaus.xfire.client.http;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.fault.XFireFault;

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
    
    /**
     * Create a REST client.
     * @param bodyHandler The handler for the XML messages.
     * @param url The url to invoke.
     */
    public RestHttpClient(ClientHandler bodyHandler, String url)
    {
        super();
        this.handler = bodyHandler;
        setUrl( url );
    }    
    
    protected void writeRequest(OutputStream out)
    {
        if ( handler.hasRequest() )
            super.writeRequest(out);
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
        throws XMLStreamException, XFireFault
    {
        handler.handleResponse(reader);
    } 
}
