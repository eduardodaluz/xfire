package org.codehaus.xfire.client.http;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.util.STAXUtils;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class EchoHandler 
    implements ClientHandler
{
    public void writeRequest(XMLStreamWriter writer) 
        throws XMLStreamException
    {
        InputStream is = getClass().getResourceAsStream("/org/codehaus/xfire/client/http/echo.xml");
        
        STAXUtils.copy( XMLInputFactory.newInstance().createXMLStreamReader( is ), writer );
    }

    public void handleResponse(XMLStreamReader reader) 
        throws XMLStreamException
    {
        // do nothing
    }

}
