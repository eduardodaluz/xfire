package org.codehaus.xfire.util.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class FragmentStreamReader
    extends DepthXMLStreamReader
{
    private boolean startDoc = false;
    private boolean startElement = false;
    private boolean middle = true;
    private boolean endDoc = false;

    private int depth;
    private int current = -1;
    
    public FragmentStreamReader(XMLStreamReader reader)
    {
        super(reader);
    }    
   
    public int getEventType()
    {
        return current;
    }

    public boolean hasNext()
        throws XMLStreamException
    {
        if (!startDoc) 
        {
            return true;
        }
        
        if (endDoc) 
        {
            return false;
        }
        
        return reader.hasNext();
    }
    
    public int next()
        throws XMLStreamException
    {
        if (!startDoc) 
        {
            startDoc = true;
            current = START_DOCUMENT;
        }
        else if (!startElement) 
        {
            depth = getDepth();
            startElement = true;
            current = START_ELEMENT;
        }
        else if (middle)
        {
            current = super.next();

            if (current == END_ELEMENT && depth < getDepth())
            {
                middle = false;
            }
        }
        else if (!endDoc)
        {
            endDoc = true;
            current = END_DOCUMENT;
        }
        else
        {
            throw new XMLStreamException("Already at the end of the document.");
        }

        return current;
    }    
}
