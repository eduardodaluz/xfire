package org.codehaus.xfire.client.http;

import java.net.URLConnection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.fault.XFireFault;

/**
 * A SOAP client for the HTTP transport.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class SoapHttpClient 
    extends AbstractHttpClient
{
    private ClientHandler bodyHandler;
    private ClientHandler headerHandler;
    private String soapVersion;
    private String action = "";
    
    /**
     * Create the SoapHttpClient. It will default to generating a
     * SOAP 1.1 Envelope.
     * 
     * @param bodyHandler A handler which will generate and process
     * the SOAP Body.
     * @param url The URL to invoke.
     */
    public SoapHttpClient(ClientHandler bodyHandler, String url)
    {
        super();
        this.bodyHandler = bodyHandler;
        setUrl( url );
        setSoapVersion( SOAP11_ENVELOPE_NS );
    }    
    
    /**
     * Create the SoapHttpClient. It will default to generating a
     * SOAP 1.1 Envelope.
     * 
     * @param bodyHandler A handler which will generate and process
     * the SOAP Body.
     * @param headerHandler A handler which will generate and process
     * the SOAP Header.
     * @param url The URL to invoke.
     */
    public SoapHttpClient( ClientHandler bodyHandler, 
                           ClientHandler headerHandler, 
                           String url )
    {
        super();
        this.bodyHandler = bodyHandler;
        this.headerHandler = headerHandler;
        setUrl( url );
        setSoapVersion( SOAP11_ENVELOPE_NS );
    }
    /**
     * Create the SoapHttpClient.
     * 
     * @param bodyHandler A handler which will generate and process
     * the SOAP Body.
     * @param headerHandler A handler which will generate and process
     * the SOAP Header.
     * @param url The URL to invoke.
     * @param soapVersion The soap version.  See SOAPConstants.
     */
    public SoapHttpClient( ClientHandler bodyHandler, 
                           ClientHandler headerHandler, 
                           String url,
                           String soapVersion )
    {
        super();
        this.bodyHandler = bodyHandler;
        this.headerHandler = headerHandler;
        this.soapVersion = soapVersion;
        setUrl( url );
    }
    
    protected void writeRequest(XMLStreamWriter writer) 
        throws XMLStreamException
    {
        writer.writeStartDocument(getEncoding(), "1.0");

        writer.setPrefix("soap", getSoapVersion());
        writer.writeStartElement("soap", "Envelope", soapVersion);
        writer.writeNamespace("soap", getSoapVersion());
        
        if ( headerHandler != null )
        {
            writer.writeStartElement("soap", "Header", soapVersion);
            headerHandler.writeRequest(writer);
            writer.writeEndElement();
        }
        
        writer.writeStartElement("soap", "Body", soapVersion);
        
        bodyHandler.writeRequest(writer);
        
        writer.writeEndElement(); // Body

        writer.writeEndElement();  // Envelope

        writer.writeEndDocument();
        writer.close();
    }

    protected void readResponse(XMLStreamReader reader) 
        throws XMLStreamException, XFireFault
    {
        while ( true )
        {
            int event = reader.next();
            switch( event )
            {
                case XMLStreamReader.END_DOCUMENT:
                    return;
                case XMLStreamReader.START_ELEMENT:
                    if( reader.getLocalName().equals("Header") && headerHandler != null )
                    {
                        reader.nextTag();
                        headerHandler.handleResponse(reader);
                    }
                    else if ( reader.getLocalName().equals("Body") )
                    {
                        reader.nextTag();
                        bodyHandler.handleResponse(reader);
                    }
                    break;
                default:
                    break;
            }
        }
    } 
    
    protected void writeHeaders(URLConnection urlConn)
    {
        // Set the SOAPAction header
        urlConn.setRequestProperty( "SOAPAction", getAction() );
    }
    
    /**
     * @return Returns the bodyHandler.
     */
    public ClientHandler getBodyHandler()
    {
        return bodyHandler;
    }
    
    /**
     * @return Returns the headerHandler.
     */
    public ClientHandler getHeaderHandler()
    {
        return headerHandler;
    }
    
    /**
     * @return Returns the soapVersion.
     */
    public String getSoapVersion()
    {
        return soapVersion;
    }
    
    /**
     * @param soapVersion The soapVersion to set.
     */
    public void setSoapVersion(String soapVersion)
    {
        this.soapVersion = soapVersion;
    }
    /**
     * @param action The action to set.
     */
    public void setAction(String action)
    {
        this.action = action;
    }
    
    public String getAction()
    {
        return action;
    }
}
