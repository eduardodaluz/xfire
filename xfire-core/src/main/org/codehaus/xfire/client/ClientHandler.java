package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.fault.XFireFault;

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
    /**
     * @return Whether or not there is a request to write.
     */
    boolean hasRequest();
    
    /**
     * Write the request to the XMLStreamWriter.
     * 
     * @param writer
     * @throws XMLStreamException
     */
    void writeRequest( XMLStreamWriter writer ) 
        throws XMLStreamException;
    
    /**
     * Handle the response.
     * @param reader
     * @throws XMLStreamException
     * @throws XFireFault The ClientHandler must create an XFireFault
     * and throw it if a fault occurs.
     */
    void handleResponse( XMLStreamReader reader )
        throws XMLStreamException, XFireFault;
    
    /**
     * Get the attachments to send with the request.
     * 
     * @return Return <code>null</code> if there are no attachments.
     */
    Attachments getAttachments();
}