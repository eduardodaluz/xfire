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
        boolean started = false;
        int read = 0; // number of elements read in
        
        while ( read > 0 || !started  )
        {
            started = true;
            int event = reader.next();
            switch( event )
            {
                case XMLStreamConstants.START_ELEMENT:
                    read++;
                    writeStartElement( reader, writer );
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    writer.writeEndElement();
                    read--;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    writer.writeCharacters( reader.getText() );  
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                case XMLStreamConstants.END_DOCUMENT:
                    throw new UnsupportedOperationException("Document should already be in START_DOCUMENT state.");
                case XMLStreamConstants.ATTRIBUTE:
                case XMLStreamConstants.NAMESPACE:
                    break;
                default:
                    break;
            }
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
                writer.setDefaultNamespace(uri);
            }
            else
            {
                writer.setPrefix( prefix, uri );
            }
            
            writer.writeStartElement( 
                prefix,
                reader.getLocalName(),
                reader.getNamespaceURI());
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
