package org.codehaus.xfire.aegis.type.basic;

import java.util.Calendar;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * Type for the Calendar class.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class CalendarType
    extends Type
{
    /**
     * @see org.codehaus.xfire.aegis.type.Type#readObject(org.dom4j.Element, MessageContext)
     */
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return reader.getValueAsCalendar();
    }

    /**
     * @see org.codehaus.xfire.aegis.type.Type#writeObject(java.lang.Object)
     */
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValueAsCalendar( (Calendar) object );
        writer.close();
    }

}
