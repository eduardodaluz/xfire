package org.codehaus.xfire.jibx;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class NoCloseXMLStreamWriter
    implements XMLStreamWriter
{

    private XMLStreamWriter writer;

    public NoCloseXMLStreamWriter(XMLStreamWriter writer)
    {
        this.writer = writer;
    }

    public void close()
        throws XMLStreamException
    {

    }

    public void flush()
        throws XMLStreamException
    {
        writer.flush();
    }

    public NamespaceContext getNamespaceContext()
    {
        return writer.getNamespaceContext();
    }

    public String getPrefix(String arg0)
        throws XMLStreamException
    {
        return writer.getPrefix(arg0);
    }

    public Object getProperty(String arg0)
        throws IllegalArgumentException
    {
        return writer.getProperty(arg0);
    }

    public void setDefaultNamespace(String arg0)
        throws XMLStreamException
    {
        writer.setDefaultNamespace(arg0);
    }

    public void setNamespaceContext(NamespaceContext arg0)
        throws XMLStreamException
    {
        writer.setNamespaceContext(arg0);
    }

    public void setPrefix(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.setPrefix(arg0, arg1);
    }

    public void writeAttribute(String arg0, String arg1, String arg2, String arg3)
        throws XMLStreamException
    {
        writer.writeAttribute(arg0, arg1, arg2, arg3);
    }

    public void writeAttribute(String arg0, String arg1, String arg2)
        throws XMLStreamException
    {
        writer.writeAttribute(arg0, arg1, arg2);
    }

    public void writeAttribute(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeAttribute(arg0, arg1);
    }

    public void writeCData(String arg0)
        throws XMLStreamException
    {
        writer.writeCData(arg0);
    }

    public void writeCharacters(char[] arg0, int arg1, int arg2)
        throws XMLStreamException
    {
        writer.writeCharacters(arg0, arg1, arg2);
    }

    public void writeCharacters(String arg0)
        throws XMLStreamException
    {
        writer.writeCharacters(arg0);
    }

    public void writeComment(String arg0)
        throws XMLStreamException
    {
        writer.writeComment(arg0);
    }

    public void writeDefaultNamespace(String arg0)
        throws XMLStreamException
    {
        writer.writeDefaultNamespace(arg0);
    }

    public void writeDTD(String arg0)
        throws XMLStreamException
    {
        writer.writeDTD(arg0);
    }

    public void writeEmptyElement(String arg0, String arg1, String arg2)
        throws XMLStreamException
    {
        writer.writeEmptyElement(arg0, arg1, arg2);
    }

    public void writeEmptyElement(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeEmptyElement(arg0, arg1);
    }

    public void writeEmptyElement(String arg0)
        throws XMLStreamException
    {
        writer.writeEmptyElement(arg0);
    }

    public void writeEndDocument()
        throws XMLStreamException
    {
        writer.writeEndDocument();
    }

    public void writeEndElement()
        throws XMLStreamException
    {
        writer.writeEndElement();
    }

    public void writeEntityRef(String arg0)
        throws XMLStreamException
    {
        writer.writeEntityRef(arg0);
    }

    public void writeNamespace(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeNamespace(arg0, arg1);
    }

    public void writeProcessingInstruction(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeProcessingInstruction(arg0, arg1);
    }

    public void writeProcessingInstruction(String arg0)
        throws XMLStreamException
    {
        writer.writeProcessingInstruction(arg0);
    }

    public void writeStartDocument()
        throws XMLStreamException
    {
        writer.writeStartDocument();
    }

    public void writeStartDocument(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeStartDocument(arg0, arg1);
    }

    public void writeStartDocument(String arg0)
        throws XMLStreamException
    {
        writer.writeStartDocument(arg0);
    }

    public void writeStartElement(String arg0, String arg1, String arg2)
        throws XMLStreamException
    {
        writer.writeStartElement(arg0, arg1, arg2);
    }

    public void writeStartElement(String arg0, String arg1)
        throws XMLStreamException
    {
        writer.writeStartElement(arg0, arg1);
    }

    public void writeStartElement(String arg0)
        throws XMLStreamException
    {
        writer.writeStartElement(arg0);
    }
}
