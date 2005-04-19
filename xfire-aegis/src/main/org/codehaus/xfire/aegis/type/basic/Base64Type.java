package org.codehaus.xfire.aegis.type.basic;

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
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        return Base64.decode(reader.getValue());
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        byte[] data = (byte[]) object;

        writer.writeValue( Base64.encode(data, 0, data.length) );
    }
}
