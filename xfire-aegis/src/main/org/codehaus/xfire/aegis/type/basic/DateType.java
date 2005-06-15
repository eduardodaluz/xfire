package org.codehaus.xfire.aegis.type.basic;

import java.util.Date;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.util.DateUtils;

/**
 * Type for the Date class which serializes as an xsd:date (no time information).
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DateType
    extends Type
{
    public Object readObject(MessageReader reader, MessageContext context)
    {
        String value = reader.getValue();
        
        if (value == null) return null;
        
        return DateUtils.parseDate( value );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValue(DateUtils.formatDate((Date)object));
    }
}
