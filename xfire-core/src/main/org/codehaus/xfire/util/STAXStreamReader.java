package org.codehaus.xfire.util;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;

/**
 * Reads a DOM4J {@link Document}, as well as other {@link Node}s, from a StAX
 * {@link XMLStreamReader}.
 * 
 * @author Dan Diephouse
 * @author Christian Niles
 */
public class STAXStreamReader
{
    /** Reference to the DocumentFactory used to build DOM4J nodes. */
    private DocumentFactory factory;
    
    /** A StAX input factory, used to construct streams from IO streams. */
    private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    
    /**
     * Constructs a default <code>STAXEventReader</code> instance with a
     * default {@link DocumentFactory}.
     */
    public STAXStreamReader()
    {
        this.factory = DocumentFactory.getInstance();
    }
    /**
     * Constructs a <code>STAXEventReader</code> instance that uses the
     * specified {@link DocumentFactory}to construct DOM4J {@link Node}s.
     * 
     * @param factory
     *            The DocumentFactory to use when constructing DOM4J nodes, or
     *            <code>null</code> if a default should be used.
     */
    public STAXStreamReader( DocumentFactory factory )
    {
        if (factory != null)
        {
            this.factory = factory;
        }
        else
        {
            this.factory = DocumentFactory.getInstance();
        }
    }
    /**
     * Sets the DocumentFactory to be used when constructing DOM4J nodes.
     * 
     * @param factory
     *            The DocumentFactory to use when constructing DOM4J nodes, or
     *            <code>null</code> if a default should be used.
     */
    public void setDocumentFactory( DocumentFactory factory )
    {
        if (factory != null)
        {
            this.factory = factory;
        }
        else
        {
            this.factory = DocumentFactory.getInstance();
        }
    }
    
    /**
     * Constructs a StAX event stream from the provided I/O stream and reads a
     * DOM4J document from it.
     * 
     * @param is
     *            The I/O stream from which the Document will be read.
     * @return The Document that was read from the stream.
     * @throws XMLStreamException
     *             If an error occurs reading content from the stream.
     */
    public Document readDocument( InputStream is ) throws XMLStreamException
    {
        return readDocument( is, null );
    }
    
    /**
     * Constructs a StAX event stream from the provided I/O character stream and
     * reads a DOM4J document from it.
     * 
     * @param reader
     *            The character stream from which the Document will be read.
     * @return The Document that was read from the stream.
     * @throws XMLStreamException
     *             If an error occurs reading content from the stream.
     */
    public Document readDocument( Reader reader ) throws XMLStreamException
    {
        return readDocument( reader, null );
    }

    public Document readDocument( InputStream is, String systemId )
            throws XMLStreamException
    {
        XMLStreamReader eventReader = inputFactory.createXMLStreamReader(
                systemId, is );
        try
        {
            return readDocument( eventReader );
        }
        finally
        {
            eventReader.close();
        }
    }
    
    /**
     * Constructs a StAX event stream from the provided I/O character stream and
     * reads a DOM4J document from it.
     * 
     * @param reader
     *            The character stream from which the Document will be read.
     * @param systemId
     *            A system id used to resolve entities.
     * @return The Document that was read from the stream.
     * @throws XMLStreamException
     *             If an error occurs reading content from the stream.
     */
    public Document readDocument( Reader reader, String systemId )
            throws XMLStreamException
    {
        XMLStreamReader eventReader = 
            inputFactory.createXMLStreamReader( systemId, reader );
        try
        {
            return readDocument( eventReader );
        }
        finally
        {
            eventReader.close();
        }
    }
    
    public void readNode( XMLStreamReader reader, Branch node ) throws XMLStreamException
    {
        int event = reader.getEventType();
        
        switch ( event )
        {
            case XMLStreamReader.START_ELEMENT:
                Element e = readElement( reader );
                node.add(e);
                break;
            case XMLStreamReader.CHARACTERS:
                node.setText( reader.getText() );
                break;
            case XMLStreamReader.PROCESSING_INSTRUCTION:
                ProcessingInstruction pi = readProcessingInstruction( reader );
                break;
            case XMLStreamReader.ENTITY_REFERENCE:
                Entity er = readEntityReference( reader );
                node.add(er);
                break;
            case XMLStreamReader.ATTRIBUTE:
            case XMLStreamReader.NAMESPACE:
            case XMLStreamReader.START_DOCUMENT:
                default:
                break;
        }
        
        
    }
    
    /**
     * Reads a DOM4J {@link Document}from the provided stream. The stream
     * should be positioned at the start of a document, or before a
     * {@link StartElement}event.
     * 
     * @param reader
     *            The event stream from which to read the {@link Document}.
     * @return The {@link Document}that was read from the stream.
     * @throws XMLStreamException
     *             If an error occurs reading events from the stream.
     */
    public Document readDocument( XMLStreamReader reader )
            throws XMLStreamException
    {
        Document doc = null;
        for ( ; reader.hasNext(); reader.next() )
        {
            int type = reader.getEventType();
            switch (type)
            {
                case XMLStreamConstants.START_DOCUMENT:
                    String encodingScheme = reader.getEncoding();
                    if ( encodingScheme != null )
                    {
                        doc = factory.createDocument( encodingScheme );
                    }
                    else
                    {
                        doc = factory.createDocument();
                    }
                    break;
                case XMLStreamConstants.END_DOCUMENT :
                case XMLStreamConstants.SPACE :
                case XMLStreamConstants.CHARACTERS :
                    // skip end document and space outside the root element
                    break;
                default :
                    if (doc == null)
                    {
                        // create document
                        doc = factory.createDocument();
                    }
                    
                    readNode( reader, (Branch) doc );
            }
        }
        
        reader.next();
        int event = reader.getEventType();
        
        return doc;
    }

    public Element readElement( XMLStreamReader reader )
            throws XMLStreamException
    {
        Element elem = createElement( reader );
        
        // read element content
        while (reader.hasNext())
        {
            reader.next();
            int event = reader.getEventType();
            switch ( event )
            {
                case XMLStreamConstants.END_ELEMENT:
                    return elem;
                default:
                    readNode( reader, elem );
                    break;
            }
            
        }
        
        return elem;
    }

    public Entity readEntityReference( XMLStreamReader reader )
            throws XMLStreamException
    {
        throw new UnsupportedOperationException("no entity ref");
    }
    
    /**
     * Constructs a DOM4J ProcessingInstruction from the provided event stream.
     * The stream must be positioned before a {@link ProcessingInstruction}
     * event.
     * 
     * @param reader
     *            The event stream from which to read the ProcessingInstruction.
     * @return The ProcessingInstruction that was read from the stream.
     * @throws XMLStreamException
     *             If an error occured reading events from the stream, or the
     *             stream was not positioned before a
     *             {@link ProcessingInstruction}event.
     */
    public org.dom4j.ProcessingInstruction readProcessingInstruction(
            XMLStreamReader reader ) throws XMLStreamException
    {
        return factory.createProcessingInstruction( 
                reader.getPIData(),
                reader.getPITarget());
    }

    public Element createElement( XMLStreamReader reader )
    {
        org.dom4j.QName elemName =
            factory.createQName( reader.getLocalName(), 
                                 reader.getPrefix(),
                                 reader.getNamespaceURI() );
        
        Element elem = factory.createElement( elemName );
        
        int count = reader.getAttributeCount();
        for ( int i = 0; i < count; i++ )
        {
            org.dom4j.QName attrName =
                factory.createQName( reader.getAttributeLocalName(i), 
                                     reader.getAttributePrefix(i),
                                     reader.getAttributeNamespace(i) );
            
            elem.addAttribute( attrName, reader.getAttributeValue(i) );
        }

        count = reader.getNamespaceCount();
        for ( int i = 0; i < count; i++ )
        {
            elem.addNamespace( reader.getNamespacePrefix(i),
                               reader.getNamespaceURI(i) );
        }
        
        return elem;
    }
}