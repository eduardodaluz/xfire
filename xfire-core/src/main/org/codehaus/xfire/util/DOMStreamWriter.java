package org.codehaus.xfire.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Writes out a org.w3c.dom tree to a STAX stream.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 21, 2004
 */
public class DOMStreamWriter
{
    /**
     * Writes an Element to an XMLStreamWriter.  The writer must already
     * have started the doucment (via writeStartDocument()).
     * 
     * @param e
     * @param writer
     * @throws XMLStreamException
     */
    public static void writeElement( Element e, XMLStreamWriter writer ) 
        throws XMLStreamException
    {
        if ( e.getNamespaceURI() != null )
        {
            boolean writeNamespace = false;
            String prefix = writer.getNamespaceContext().getPrefix(e.getNamespaceURI());
            if ( prefix == null )
            {
                prefix = e.getPrefix();
                writeNamespace = true;
            }
            
            if ( prefix == null )
            {
                writer.setDefaultNamespace(e.getNamespaceURI());
                writeNamespace = false;
                writer.writeStartElement( e.getNodeName() );
                writer.writeDefaultNamespace(e.getNamespaceURI());
            }
            else
            {
                writer.writeStartElement( prefix + ":" + e.getNodeName() );
            }
            
            if ( writeNamespace )
            {
                writer.writeNamespace(prefix, e.getNamespaceURI());
            }
        }
        else
        {
            writer.writeStartElement( e.getNodeName() );
        }
        
        NamedNodeMap attrs = e.getAttributes();
        for ( int i = 0; i < attrs.getLength(); i++ )
        {
            Node attr = attrs.item(i);
            
            boolean writeAttrNamespace = false;
            String attrPrefix = writer.getNamespaceContext().getPrefix(attr.getNamespaceURI());
            if ( attrPrefix == null )
            {
                writeAttrNamespace = true;
                attrPrefix = attr.getPrefix();
            }
            
            writer.writeAttribute(attrPrefix, attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
            
            if ( writeAttrNamespace )
            {
                writer.writeNamespace(attrPrefix, e.getNamespaceURI());
            }
        }

        String value = e.getNodeValue();
        if ( value != null )
            writer.writeCharacters( value );
        
        NodeList nodes = e.getChildNodes();
        for ( int i = 0; i < nodes.getLength(); i++ )
        {
            Node n = nodes.item(i);
            if ( n instanceof Element )
            {
                writeElement((Element)e, writer);
            }
        }
        writer.writeEndElement();
    }
}
