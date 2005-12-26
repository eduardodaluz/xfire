package org.codehaus.xfire.aegis.type.basic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.Base64;

/**
 * Converts back and forth to byte[] objects.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class Base64Type
    extends Type
{
    public Base64Type()
    {
        super();
        setNillable(true);
    }

    public Object readObject(MessageReader mreader, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = mreader.getXMLStreamReader();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        try
        {
            int event = reader.next();
            if (!reader.isCharacters() && !reader.isWhiteSpace()) return null;
            
            int length = 1024;
            char[] myBuffer = new char[length];
            for (int sourceStart = 0;; sourceStart += length)
            {
                int nCopied = reader.getTextCharacters(sourceStart, myBuffer, 0, length);
                
                Base64.decode(myBuffer, 0, nCopied, bos);
                
                if (nCopied < length)
                    break;
            }
            
            while (reader.getEventType() != XMLStreamReader.END_ELEMENT) reader.next();
            
            // Advance just past the end element
            reader.next();
            
            return bos.toByteArray();
        }
        catch (IOException e)
        {
            throw new XFireFault("Could not parse base64Binary data.", e, XFireFault.SENDER);
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not parse base64Binary data.", e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        byte[] data = (byte[]) object;

        writer.writeValue( Base64.encode(data, 0, data.length) );
    }
}
