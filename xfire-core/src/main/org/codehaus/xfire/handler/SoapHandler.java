package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.SOAPConstants;

/**
 * Delegates the SOAP Body and Header to appropriate handlers.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 28, 2004
 */
public class SoapHandler 
    extends AbstractHandler
{
    private Handler bodyHandler;
    private Handler headerHandler;

    public SoapHandler( Handler bodyHandler )
    {
        this.bodyHandler = bodyHandler;
    }
    
    public SoapHandler( Handler bodyHandler, Handler headerHandler )
    {
        this.bodyHandler = bodyHandler;
        this.headerHandler = headerHandler;
    }
    
    /**
     * Invoke the Header and Body Handlers for the SOAP message.
     */
    public void invoke(MessageContext context, XMLStreamReader reader)
            throws Exception
    {
        XMLStreamWriter writer = getXMLStreamWriter(context);
        writer.writeStartDocument("UTF-8", "1.0");
        String soapVersion = SOAPConstants.SOAP11_ENVELOPE_NS;
        
        writer.setPrefix("soap", soapVersion);
        writer.writeStartElement("soap", "Envelope", soapVersion);
        writer.writeNamespace("soap", soapVersion);

        if ( headerHandler != null )
        {
            writer.writeStartElement("soap", "Header", soapVersion);
            //headerHandler.writeRequest(writer);
            writer.writeEndElement();
        }
        
        writer.writeStartElement("soap", "Body", soapVersion);
        
        boolean end = false;
        while ( !end )
        {
            int event = reader.next();
            switch( event )
            {
            case XMLStreamReader.END_DOCUMENT:
                end = true;
                break;
            case XMLStreamReader.START_ELEMENT:
                if( reader.getLocalName().equals("Header") && headerHandler != null )
                {
                    reader.nextTag();
                    headerHandler.invoke(context, reader);
                }
                else if ( reader.getLocalName().equals("Body") )
                {
                    reader.nextTag();
                    bodyHandler.invoke(context, reader);
                }
                break;
            default:
                break;
            }
        }

        writer.writeEndElement(); // Body

        writer.writeEndElement();  // Envelope

        writer.writeEndDocument();
        writer.close();
    }
}
