package org.codehaus.xfire.aegis.type.basic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Document;

/**
 * Reads and writes <code>org.w3c.dom.Document</code> types. 
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DocumentType
    extends Type
{
    private DocumentBuilder builder;
    
    public DocumentType()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
           throw new XFireRuntimeException("Couldn't load document builder.", e);
        }
    }
    
    public DocumentType(DocumentBuilder builder)
    {
        this.builder = builder;
    }

    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            return STAXUtils.read(builder, ((ElementReader) reader).getXMLStreamReader()) ;
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not parse xml.", e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        Document doc = (Document) object;
        
        try
        {
            STAXUtils.writeElement(doc.getDocumentElement(), 
                                   ((ElementWriter) writer).getXMLStreamWriter());
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write xml.", e, XFireFault.SENDER);
        }
    }
}
