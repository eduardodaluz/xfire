package org.codehaus.xfire.client.http;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.client.ClientHandler;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class EchoHandler 
    implements ClientHandler
{
    public XMLStreamReader createRequest() 
        throws XMLStreamException
    {
        InputStream is = getClass().getResourceAsStream("/org/codehaus/xfire/client/http/echo.xml");
        
        return XMLInputFactory.newInstance().createXMLStreamReader( is );
    }

    public void handleResponse(XMLStreamReader reader) 
        throws XMLStreamException
    {
        // do nothing
    }
}
