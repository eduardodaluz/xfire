package org.codehaus.xfire.aegis.type.basic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.date.XsDateTimeFormat;

/**
 * Type for the Time class which serializes to an xs:time.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TimestampType
    extends Type
{
    private static XsDateTimeFormat format = new XsDateTimeFormat();
    
    public Object readObject(MessageReader reader, MessageContext context) throws XFireFault
    {
        String value = reader.getValue();
        
        if (value == null) return null;
        
        try
        {
            Calendar c = (Calendar) format.parseObject(value);
            return new Timestamp(c.getTimeInMillis());
        }
        catch (ParseException e)
        {
            throw new XFireFault("Could not parse xs:dateTime: " + e.getMessage(), e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        Calendar c = Calendar.getInstance();
        c.setTime((Timestamp) object);
        writer.writeValue(format.format(c));
    }
}
