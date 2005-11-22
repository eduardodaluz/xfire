package org.codehaus.xfire.xmpp;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.XFireRuntimeException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlPullStreamReader
    implements XMLStreamReader
{
    private XmlPullParser parser;
    private int current;
    private int startDepth;
    private String local;
    private String namespace;
    private String prefix;
    
    public XmlPullStreamReader(XmlPullParser parser)
    {
        this.parser = parser;
        startDepth = parser.getDepth();
        
        try
        {
            current = xmlpullToStaxEvent(parser.getEventType());
        }
        catch (XmlPullParserException e)
        {
            throw new XFireRuntimeException("Couldn't convert to stax event.", e);
        }
    }

    public void close()
        throws XMLStreamException
    {
    }

    public int getAttributeCount()
    {
        return parser.getAttributeCount();
    }

    public String getAttributeLocalName(int index)
    {
        return parser.getAttributeName(index);
    }

    public QName getAttributeName(int index)
    {
        String prefix = getAttributePrefix(index);
        if (prefix != null && prefix.length() > 0)
        {
            return new QName(prefix, getAttributeNamespace(index), getAttributeLocalName(index));
        }
        else
        {
            return new QName(getAttributeNamespace(index), getAttributeLocalName(index));
        }
    }

    public String getAttributeNamespace(int index)
    {
        return parser.getAttributeNamespace(index);
    }

    public String getAttributePrefix(int index)
    {
        return parser.getAttributePrefix(index);
    }

    public String getAttributeType(int index)
    {
        return parser.getAttributeType(index);
    }

    public String getAttributeValue(int index)
    {
        return parser.getAttributeValue(index);
    }

    public String getAttributeValue(String ns, String local)
    {
        return parser.getAttributeValue(ns, local);
    }

    public String getCharacterEncodingScheme()
    {
        return parser.getInputEncoding();
    }

    public String getElementText()
        throws XMLStreamException
    {
        return parser.getText();
    }

    public String getEncoding()
    {
        return parser.getInputEncoding();
    }

    public int getEventType()
    {
        return current;
    }

    public String getLocalName()
    {
        return local;
    }

    public Location getLocation()
    {
        return null;
    }

    public QName getName()
    {
        //if (current != START_ELEMENT || current != END_ELEMENT)
        //    throw new IllegalStateException("Must be at a start or end element");

        if (prefix != null && prefix.length() > 0)
        {
            return new QName(namespace, local, prefix);
        }
        else
        {
            return new QName(namespace, local);
        }
    }

    public NamespaceContext getNamespaceContext()
    {
        throw new IllegalStateException();
    }

    public int getNamespaceCount()
    {
        try
        {
            return parser.getNamespaceCount(parser.getDepth());
        }
        catch (XmlPullParserException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getNamespacePrefix(int index)
    {
        try
        {
            return parser.getNamespacePrefix(index);
        }
        catch (XmlPullParserException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getNamespaceURI()
    {
        return namespace;
    }

    public String getNamespaceURI(int index)
    {
        try
        {
            return parser.getNamespaceUri(index);
        }
        catch (XmlPullParserException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getNamespaceURI(String prefix)
    {
        return parser.getNamespace(prefix);
    }

    public String getPIData()
    {
        // How do I get at this?
        throw new UnsupportedOperationException();
    }

    public String getPITarget()
    {
        // How do I get at this?
        throw new UnsupportedOperationException();
    }

    public String getPrefix()
    {
        return prefix;
    }

    public Object getProperty(String name)
        throws IllegalArgumentException
    {
        return parser.getProperty(name);
    }

    public String getText()
    {
        return parser.getText();
    }

    public char[] getTextCharacters()
    {
        return parser.getText().toCharArray();
    }

    public int getTextCharacters(int start, char[] target, int sourceStart, int length)
        throws XMLStreamException
    {
        throw new UnsupportedOperationException();
    }

    public int getTextLength()
    {
        return getText().length();
    }

    public int getTextStart()
    {
        return 0;
    }

    public String getVersion()
    {
        return null;
    }

    public boolean hasName()
    {
        return (current == START_ELEMENT || current == END_ELEMENT);
    }

    public boolean hasNext()
        throws XMLStreamException
    {
        return (current != END_DOCUMENT ||
                 !(current == END_ELEMENT && parser.getDepth() == startDepth));
    }

    public boolean hasText()
    {
        return (current == CHARACTERS ||
                current == DTD ||
                current == ENTITY_REFERENCE ||
                current == COMMENT ||
                current == SPACE);
    }

    public boolean isAttributeSpecified(int index)
    {
        if (current != START_ELEMENT || current != ATTRIBUTE)
            throw new IllegalStateException();
        
        return true;
    }

    public boolean isCharacters()
    {
        return current == CHARACTERS;
    }

    public boolean isEndElement()
    {
        return current == END_ELEMENT;
    }

    public boolean isStandalone()
    {
        return false;
    }

    public boolean isStartElement()
    {
        return current == START_ELEMENT;
    }

    public boolean isWhiteSpace()
    {
        return current == SPACE;
    }

    public int next()
        throws XMLStreamException
    {
        try
        {
            current = xmlpullToStaxEvent(parser.next());
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException) 
                throw (RuntimeException) e;
            
            e.printStackTrace();
            
            throw new XMLStreamException(e);
        }
        
        return current;
    }

    protected int xmlpullToStaxEvent(int xpe)
    {
        if (xpe == XmlPullParser.CDSECT)
        {
            return CDATA;
        }
        else if (xpe == XmlPullParser.COMMENT)
        {
            return COMMENT;
        }
        else if (xpe == XmlPullParser.DOCDECL)
        {
            return COMMENT;
        }
        else if (xpe == XmlPullParser.END_DOCUMENT)
        {
            return END_DOCUMENT;
        }
        else if (xpe == XmlPullParser.END_TAG)
        {
            return END_ELEMENT;
        }
        else if (xpe == XmlPullParser.ENTITY_REF)
        {
            return ENTITY_DECLARATION;
        }
        else if (xpe == XmlPullParser.IGNORABLE_WHITESPACE)
        {
            return SPACE;
        }
        else if (xpe == XmlPullParser.PROCESSING_INSTRUCTION)
        {
            return PROCESSING_INSTRUCTION;
        }
        else if (xpe == XmlPullParser.START_DOCUMENT)
        {
            return START_DOCUMENT;
        }
        else if (xpe == XmlPullParser.START_TAG)
        {
            local = parser.getName();
            prefix = parser.getPrefix();
            namespace = parser.getNamespace(prefix);
            
            return START_ELEMENT;
        }
        else if (xpe == XmlPullParser.TEXT)
        {
            return CHARACTERS;
        }

        throw new IllegalStateException("Unknown event type " + xpe);
    }
    
    public int nextTag()
        throws XMLStreamException
    {
        int eventType = next();
        while ((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace())
                || (eventType == XMLStreamConstants.CDATA && isWhiteSpace())
                // skip whitespace
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT)
        {
            eventType = next();
        }
        if (eventType != XMLStreamConstants.START_ELEMENT
                && eventType != XMLStreamConstants.END_ELEMENT)
        {
            throw new XMLStreamException("expected start or end tag", getLocation());
        }
        return eventType;
    }

    public void require(int index, String ns, String local)
        throws XMLStreamException
    {
        if (current != index) 
            throw new XMLStreamException("Event type does not match.");
        
        if (ns != null)
        {
            if (!getNamespaceURI().equals(ns))
                throw new XMLStreamException("Namespace does not match.");
        }
        
        if (local != null)
        {
            if (!getLocalName().equals(local))
                throw new XMLStreamException("Local name does not match.");
        }
    }

    public boolean standaloneSet()
    {
        Boolean s = (Boolean) 
            getProperty("http://xmlpull.org/v1/doc/features.html#xmldecl-standalone");
        if (s == null || !s.booleanValue()) return false;
        
        return true;
    }
}
