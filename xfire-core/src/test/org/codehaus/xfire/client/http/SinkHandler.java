package org.codehaus.xfire.client.http;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.NullRequestHandler;
import org.codehaus.xfire.util.STAXUtils;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class SinkHandler
    extends NullRequestHandler
{
    private String response;
    
    public void handleResponse(XMLStreamReader reader) throws XMLStreamException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);
        
        STAXUtils.copy(reader, writer);
        
        writer.close();
        
        this.response = out.toString(); 
    }

    
    /**
     * @return Returns the response.
     */
    public String getResponse()
    {
        return response;
    }
}
