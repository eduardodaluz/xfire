package org.codehaus.xfire.aegis.type.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Reads and writes <code>org.w3c.dom.Document</code> types. 
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLStreamReaderType
    extends Type
{
    public XMLStreamReaderType()
    {
        setWriteOuter(false);
    }

    public Object readObject(MessageReader mreader, MessageContext context)
        throws XFireFault
    {
        return ((ElementReader) mreader).getXMLStreamReader();
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = (XMLStreamReader) object;
        
        try
        {
            STAXUtils.copy(reader, ((ElementWriter) writer).getXMLStreamWriter());
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write xml.", e, XFireFault.SENDER);
        }
    }
}
