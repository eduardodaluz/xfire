package org.codehaus.xfire.aegis.type.basic;

import java.util.Date;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * Type for the Calendar class.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DateType
    extends Type
{
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return reader.getValueAsDateTime();
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValueAsDateTime( (Date) object );
    }
}
