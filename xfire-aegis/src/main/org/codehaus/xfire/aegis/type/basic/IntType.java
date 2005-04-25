package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * SimpleSerializer
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class IntType
    extends Type
{
    public Object readObject(MessageReader reader, MessageContext context)
    {
        if( null == reader.getValue() )
        {
            return null;
        }
        else
        {
            return new Integer( reader.getValueAsInt() );
        }
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValueAsInt( (Integer) object );
    }
}