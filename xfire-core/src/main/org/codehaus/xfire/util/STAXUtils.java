package org.codehaus.xfire.util;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Common StAX utilities.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class STAXUtils
{
    public static void copy( XMLStreamReader reader, XMLStreamWriter writer ) 
        throws XMLStreamException
    {
        int read = 0; // number of elements read in
        int event = reader.getEventType();
        
        while ( true )
        {
            switch( event )
            {
                case XMLStreamConstants.START_ELEMENT:
                    read++;
                    writeStartElement( reader, writer );
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    writer.writeEndElement();
                    read--;
                    if ( read <= 0 )
                        return;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    writer.writeCharacters( reader.getText() );  
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                case XMLStreamConstants.END_DOCUMENT:
                case XMLStreamConstants.ATTRIBUTE:
                case XMLStreamConstants.NAMESPACE:
                    break;
                default:
                    break;
            }
            event = reader.next();
        }
    }

    private static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer) 
        throws XMLStreamException
    {
        String prefix = reader.getPrefix();
        if ( prefix == null )
        {
            prefix = "";
        }
        
        String uri = reader.getNamespaceURI();
        if ( uri != null )
        {
            if ( prefix.equals("") )
            {
                String defNS = writer.getNamespaceContext().getNamespaceURI("");
                if ( defNS != null && !defNS.equals(uri) )
                {
                    writer.setDefaultNamespace(uri);
                    writer.writeStartElement( uri, reader.getLocalName() );
                    writer.writeDefaultNamespace( uri );
                }
                else
                {
                    writer.writeStartElement( uri, reader.getLocalName() );
                }  
            }
            else
            {
                writer.setPrefix( prefix, uri );
                
                writer.writeStartElement( 
                    prefix,
                    reader.getLocalName(),
                    uri);
            }
        }
        else
        {
            writer.writeStartElement( reader.getLocalName() );
        }

        for ( int i = 0; i < reader.getAttributeCount(); i++ )
        {
            writer.writeAttribute(
                reader.getAttributeNamespace(i),
                reader.getAttributeLocalName(i),
                reader.getAttributeValue(i));
        }
        
        for ( int i = 0; i < reader.getNamespaceCount(); i++ )
        {
            writer.writeNamespace(
                reader.getNamespacePrefix(i),
                reader.getNamespaceURI(i));
        }
    }
}
