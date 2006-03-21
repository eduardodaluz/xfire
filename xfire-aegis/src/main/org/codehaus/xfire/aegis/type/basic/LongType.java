package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/** 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class LongType
    extends Type
{
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return new Long( reader.getValueAsLong() );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        if (object instanceof Long)
        {
            writer.writeValueAsLong( (Long) object );
        }
        else
        {
            writer.writeValueAsLong(new Long(((Number)object).longValue()));
        }
    }
}
