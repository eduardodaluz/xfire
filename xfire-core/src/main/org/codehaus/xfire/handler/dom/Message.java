package org.codehaus.xfire.handler.dom;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * Holds a SOAP message as a DOM4J document. There are also 
 * helper methods to grab the SOAP Body and/or Header.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class Message
{
    private Document message;
    
    private Element body;
    
    private Element header;
    
    private String soapVersion;
    
    public Message( Document message )
    {
        this.message = message;

        soapVersion = message.getRootElement().getNamespaceURI();
    }

	/**
	 * Creates a SOAP Envelope, Body and Header for the appropriate SOAP version.
	 */
	public Message( String soapVersion )
	{
	    message = DocumentHelper.createDocument();
        
	    Namespace soapNamespace = Namespace.get( "soap", soapVersion );

        this.soapVersion = soapVersion;
		
		Element envelope = message.addElement( new QName("Envelope", soapNamespace) );
        header = envelope.addElement( new QName("Header", soapNamespace) );
        
        body = envelope.addElement( new QName("Body", soapNamespace) );
	}
    
	/**
	 * @return Returns the body.
	 */
	public Element getBody()
	{
        if ( body == null )
        {
            body = message.getRootElement().element( "Body" );  
        }
		return body;
	}
    
	/**
	 * @return Returns the header. If it doesn't exist, it is created.
	 */
	public Element getHeader()
	{
        if ( header == null )
        {
             header = message.getRootElement().element( "Header" );
        }
		return header;
	}
    
	/**
	 * @return Returns the message.
	 */
	public Document getMessage()
	{
		return message;
	}
    
	/**
	 * @return Returns the soapVersion.
	 */
	public String getSoapVersion()
	{
		return soapVersion;
	}
}
