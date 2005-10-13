package org.codehaus.xfire.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.yom.Attribute;
import org.codehaus.yom.Comment;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

public class ElementStreamWriter
    implements XMLStreamWriter
{
    private Stack stack = new Stack();
    private Document document;
    private Element currentNode;
    private NamespaceContext context;
    private Map properties = new HashMap();
    
    public ElementStreamWriter()
    {
    }
    
    public ElementStreamWriter(Element e)
    {
        newChild(e);
    }

    public void writeStartElement(String local)
        throws XMLStreamException
    {
        newChild(new Element(local));
    }

    private void newChild(Element element)
    {
        if (currentNode != null)
        {
            stack.push(currentNode);
            currentNode.appendChild(element);
        }
        
        YOMNamespaceContext context = new YOMNamespaceContext();
        context.currentNode = element;
        this.context = context;
        
        currentNode = element;
    }

    public void writeStartElement(String namespace, String local)
        throws XMLStreamException
    {
        newChild(new Element(local, namespace));
    }

    public void writeStartElement(String prefix, String local, String namespace)
        throws XMLStreamException
    {
        if (prefix == null || prefix.equals(""))
            writeStartElement(namespace, local);

        newChild(new Element(prefix + ":" + local, namespace));
    }

    public void writeEmptyElement(String namespace, String local)
        throws XMLStreamException
    {
        writeStartElement(namespace, local);
    }

    public void writeEmptyElement(String prefix, String namespace, String local)
        throws XMLStreamException
    {
        writeStartElement(prefix, namespace, local);
    }

    public void writeEmptyElement(String local)
        throws XMLStreamException
    {
        writeStartElement(local);
    }

    public void writeEndElement()
        throws XMLStreamException
    {
        currentNode = (Element) stack.pop();
    }

    public void writeEndDocument()
        throws XMLStreamException
    {
    }

    public void close()
        throws XMLStreamException
    {
    }

    public void flush()
        throws XMLStreamException
    {
    }

    public void writeAttribute(String local, String value)
        throws XMLStreamException
    {
        currentNode.addAttribute(new Attribute(local, value));
    }

    public void writeAttribute(String prefix, String namespace, String local, String value)
        throws XMLStreamException
    {
        currentNode.addAttribute(new Attribute(prefix + ":" + local, namespace, value));
    }

    public void writeAttribute(String namespace, String local, String value)
        throws XMLStreamException
    {
        currentNode.addAttribute(new Attribute(local, namespace, value));
    }

    public void writeNamespace(String prefix, String namespace)
        throws XMLStreamException
    {
        String decNS = currentNode.getNamespaceURI(prefix);
        if (decNS != null && !decNS.equals(namespace))
            currentNode.addNamespaceDeclaration(prefix, namespace);
    }

    public void writeDefaultNamespace(String namespace)
        throws XMLStreamException
    {
        currentNode.addNamespaceDeclaration("", namespace);
    }

    public void writeComment(String value)
        throws XMLStreamException
    {
        currentNode.appendChild(new Comment(value));
    }

    public void writeProcessingInstruction(String arg0)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        
    }

    public void writeProcessingInstruction(String arg0, String arg1)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        
    }

    public void writeCData(String arg0)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        
    }

    public void writeDTD(String arg0)
        throws XMLStreamException
    {
        
    }

    public void writeEntityRef(String arg0)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        
    }

    public void writeStartDocument()
        throws XMLStreamException
    {
        document = new Document(new Element("root"));
    }

    public void writeStartDocument(String version)
        throws XMLStreamException
    {
        writeStartDocument();
        
        // TODO: set encoding/version
    }

    public void writeStartDocument(String encoding, String version)
        throws XMLStreamException
    {
        writeStartDocument();
        
        // TODO: set encoding/version
    }

    public void writeCharacters(String text)
        throws XMLStreamException
    {
        currentNode.appendChild(text);
    }

    public void writeCharacters(char[] text, int start, int len)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        currentNode.appendChild(new String(text, start, len));
    }

    public String getPrefix(String uri)
        throws XMLStreamException
    {
        return currentNode.getNamespacePrefix(uri);
    }

    public void setPrefix(String arg0, String arg1)
        throws XMLStreamException
    {
    }

    public void setDefaultNamespace(String arg0)
        throws XMLStreamException
    {
    }

    public void setNamespaceContext(NamespaceContext context)
        throws XMLStreamException
    {
        this.context = context;
    }

    public NamespaceContext getNamespaceContext()
    {
        return context;
    }

    public Object getProperty(String prop)
        throws IllegalArgumentException
    {
        return properties.get(prop);
    }
    
    protected static class YOMNamespaceContext implements NamespaceContext
    {
        public Element currentNode;
        
        public String getNamespaceURI(String prefix)
        {
            return currentNode.getNamespaceURI(prefix);
        }

        public String getPrefix(String uri)
        {
            return currentNode.getNamespacePrefix(uri);
        }

        public Iterator getPrefixes(String uri)
        {
            List prefixes = new ArrayList();
            
            String prefix = getPrefix(uri);
            if (prefix != null) prefixes.add(prefix);
            
            return prefixes.iterator();
        }
        
    }
}
