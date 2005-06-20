package org.codehaus.xfire.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;
import org.codehaus.yom.Node;
import org.codehaus.yom.Text;

import com.bea.xml.stream.util.Stack;

public class ElementStreamReader
    implements XMLStreamReader
{
    public Map properties = new HashMap();
    
    private Stack frames = new Stack();
    private ElementFrame frame;
    private String text;
    private int currentEvent;
    
    public class ElementFrame
    {
        Element element;
        
        boolean started = false;
        boolean ended = false;
        
        int currentChild = -1;
        int currentAttribute = -1;
        int currentNamespace = -1;
    }

    public ElementStreamReader(Element e)
    {
        frame = new ElementFrame();
        frame.element = e;
        frames.push(this.frame);
    }
    
    public Object getProperty(String key)
        throws IllegalArgumentException
    {
        return properties.get(key);
    }

    public int next()
        throws XMLStreamException
    {
        if (!frame.started)
        {
            frame.started = true;
            currentEvent = START_ELEMENT;
        }
        else if (frame.currentAttribute < frame.element.getAttributeCount() - 1)
        {
            frame.currentAttribute++;
            currentEvent = ATTRIBUTE;            
        }
        else if (frame.currentNamespace < frame.element.getNamespaceDeclarationCount() - 1)
        {
            frame.currentNamespace++;
            currentEvent = NAMESPACE;
        }
        else if (frame.currentChild < frame.element.getChildCount() - 1)
        {
            frame.currentChild++;
            Node currentChildNode = frame.element.getChild(frame.currentChild);
            
            if (currentChildNode instanceof Text)
            {
                text = currentChildNode.getValue();
                currentEvent = CHARACTERS;
            }
            else if (currentChildNode instanceof Element)
            {
                ElementFrame newFrame = new ElementFrame();
                newFrame.element = (Element) currentChildNode;
                newFrame.started = true;
                frame = newFrame;
                frames.push(this.frame);
                
                currentEvent = START_ELEMENT;
            }
            else
            {
                throw new RuntimeException("Unknown node type: " + 
                                           currentChildNode.getClass().getName());
            }
        }
        else
        {
            frame = (ElementFrame) frames.pop();
            
            currentEvent = END_ELEMENT;
        }
         
        return currentEvent;
    }

    public void require(int arg0, String arg1, String arg2)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        
    }

    public String getElementText()
        throws XMLStreamException
    {
        return text;
    }

    public int nextTag()
        throws XMLStreamException
    {
        while (hasNext())
        {
            if (START_ELEMENT == next()) return START_ELEMENT;
        }
        
        return currentEvent;
    }

    public boolean hasNext()
        throws XMLStreamException
    {
        return !(frames.size() == 0 && currentEvent == -1);
    }

    public void close()
        throws XMLStreamException
    {
    }

    public String getNamespaceURI(String prefix)
    {
        return frame.element.getNamespaceURI(prefix);
    }

    public boolean isStartElement()
    {
        return (currentEvent == START_ELEMENT);
    }

    public boolean isEndElement()
    {
        return (currentEvent == END_ELEMENT);
    }

    public boolean isCharacters()
    {
        return (currentEvent == CHARACTERS);
    }

    public boolean isWhiteSpace()
    {
        return (currentEvent == SPACE);
    }

    public String getAttributeValue(String ns, String local)
    {
        Attribute att = frame.element.getAttribute(local, ns);
        if (att != null) return att.getValue();
        
        return null;
    }

    public int getAttributeCount()
    {
        return frame.element.getAttributeCount();
    }

    public QName getAttributeName(int index)
    {
        Attribute att = frame.element.getAttribute(index);
        
        String local = att.getLocalName();
        String prefix = att.getNamespacePrefix();
        String ns = att.getNamespaceURI();
        
        if (ns != null)
        {
            if (prefix != null)
            {
                return new QName(ns, local, prefix);
            }
            else
            {
                return new QName(ns, local);
            }
        }
        
        return new QName(local);
    }

    public String getAttributeNamespace(int index)
    {
        return frame.element.getAttribute(index).getNamespaceURI();
    }

    public String getAttributeLocalName(int index)
    {
        return frame.element.getAttribute(index).getLocalName();
    }

    public String getAttributePrefix(int index)
    {
        return frame.element.getAttribute(index).getNamespacePrefix();
    }

    public String getAttributeType(int index)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAttributeValue(int index)
    {
        return frame.element.getAttribute(index).getValue();
    }

    public boolean isAttributeSpecified(int index)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public int getNamespaceCount()
    {
        return frame.element.getNamespaceDeclarationCount();
    }

    public String getNamespacePrefix(int ns)
    {
        return frame.element.getNamespacePrefix(ns);
    }

    public String getNamespaceURI(int count)
    {
        Element e = frame.element;
        return e.getNamespaceURI(e.getNamespacePrefix(count));
    }

    public NamespaceContext getNamespaceContext()
    {
        throw new UnsupportedOperationException();
    }

    public int getEventType()
    {
        return currentEvent;
    }

    public String getText()
    {
        return text;
    }

    public char[] getTextCharacters()
    {
        return text.toCharArray();
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextStart()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getEncoding()
    {
        return null;
    }

    public boolean hasText()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Location getLocation()
    {
        return null;
    }

    public QName getName()
    {
        String local = frame.element.getLocalName();
        String prefix = frame.element.getNamespacePrefix();
        String ns = frame.element.getNamespaceURI();
        
        if (ns != null)
        {
            if (prefix != null)
            {
                return new QName(ns, local, prefix);
            }
            else
            {
                return new QName(ns, local);
            }
        }
        
        return new QName(local);
    }

    public String getLocalName()
    {
        return frame.element.getLocalName();
    }

    public boolean hasName()
    {
        return (currentEvent == START_ELEMENT || currentEvent == END_ELEMENT);
    }

    public String getNamespaceURI()
    {
        return frame.element.getNamespaceURI();
    }

    public String getPrefix()
    {
        return frame.element.getNamespacePrefix();
    }

    public String getVersion()
    {
        return null;
    }

    public boolean isStandalone()
    {
        return false;
    }

    public boolean standaloneSet()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public String getCharacterEncodingScheme()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPITarget()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPIData()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
