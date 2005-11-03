package org.codehaus.xfire.util.jdom;

import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.Text;

public class StaxSerializer
{
    public void writeDocument(Document doc, XMLStreamWriter writer)
        throws XMLStreamException
    {
        writer.writeStartDocument("1.0");        

        for (Iterator itr = doc.getContent().iterator(); itr.hasNext();)
        {
            Content content = (Content) itr.next();
            
            if (content instanceof Element)
                writeElement((Element) content, writer);
        }
        
        writer.writeEndDocument();
    }

    public void writeElement(Element e, XMLStreamWriter writer)
        throws XMLStreamException
    {
        // need to check if the namespace is declared before we write the
        // start element because that will put the namespace in the context.
        String elPrefix = e.getNamespacePrefix();
        String elUri = e.getNamespaceURI();

        String boundPrefix = writer.getPrefix(elUri);
        boolean writeElementNS = false;
        if ( boundPrefix == null || !elPrefix.equals(boundPrefix) )
        {   
            writeElementNS = true;
        }
        
        writer.writeStartElement(elPrefix, e.getName(), elUri);

        List namespaces = e.getAdditionalNamespaces();
        for (Iterator itr = namespaces.iterator(); itr.hasNext();)
        {
            Namespace ns = (Namespace) itr.next();

            String prefix = ns.getPrefix();
            String uri = ns.getURI();
            
            writer.writeNamespace(prefix, uri);
            
            if (elUri.equals(uri) && elPrefix.equals(prefix))
            {
                writeElementNS = false;
            }
        }

        if (writeElementNS)
        {
            if ( elPrefix == null || elPrefix.length() ==  0 )
            {
                writer.writeDefaultNamespace(elUri);
            }
            else
            {
                writer.writeNamespace(elPrefix, elUri);
            }
        }
        
        for (Iterator itr = e.getAttributes().iterator(); itr.hasNext();)
        {
            Attribute attr = (Attribute) itr.next();
            String attPrefix= attr.getNamespacePrefix();
            String attUri = attr.getNamespaceURI();
            
            if (attUri == null)
                writer.writeAttribute(attr.getName(), attr.getValue());
            else
                writer.writeAttribute(attPrefix, attUri, attr.getName(), attr.getValue());
        }

        for (Iterator itr = e.getContent().iterator(); itr.hasNext();)
        {
            Content n = (Content) itr.next();
            if (n instanceof Text)
            {
                writer.writeCharacters(((Text) n).getText());
            }
            else if (n instanceof Element)
            {
                writeElement((Element) n, writer);
            }
            else if (n instanceof Comment)
            {
                writer.writeComment(n.getValue());
            }
            else if (n instanceof CDATA)
            {
                writer.writeCData(n.getValue());
            }
            else if (n instanceof EntityRef)
            {
                EntityRef ref = (EntityRef) n;
                //writer.writeEntityRef(ref.)
            }
        }

        writer.writeEndElement();
    }

    /**
     * @param writer
     * @param prefix
     * @param uri
     * @throws XMLStreamException
     */
    private boolean isDeclared(XMLStreamWriter writer, String prefix, String uri)
        throws XMLStreamException
    {
        String decPrefix = writer.getPrefix(uri);
        return (decPrefix != null && decPrefix.equals(prefix));
    }
}