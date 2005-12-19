package org.codehaus.xfire.aegis.type.basic;

import java.util.Date;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Type for the java.sql.Date class which serializes as an xsd:date (no time information).
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SqlDateType
    extends DateType
{
    public Object readObject(MessageReader reader, MessageContext context) throws XFireFault
    {
        Date date = ((Date) super.readObject(reader, context));
        if (date == null) return null;
        
        return new java.sql.Date(date.getTime());
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        java.sql.Date date = (java.sql.Date) object;
        
        super.writeObject(new Date(date.getTime()), writer, context);
    }   
}
