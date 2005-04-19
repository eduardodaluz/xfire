package org.codehaus.xfire.aegis;

import java.util.Calendar;
import java.util.Date;

import org.codehaus.xfire.util.DateUtils;

/**
 * Basic type conversion functionality for writing messages.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractMessageWriter
        implements MessageWriter
{
    /**
     * Create a LiteralWriter but without writing an element name.
     *
     * @param writer
     */
    public AbstractMessageWriter()
    {
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsCalendar(java.util.Calendar)
     */
    public void writeValueAsCalendar(Calendar calendar)
    {
        writeValue(DateUtils.formatDateTime(calendar.getTime()));
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsInt(java.lang.Integer)
     */
    public void writeValueAsInt(Integer i)
    {
        writeValue(i.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsDate(java.util.Date)
     */
    public void writeValueAsDateTime(Date date)
    {
        writeValue(DateUtils.formatDateTime(date));
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsDate(java.util.Date)
     */
    public void writeValueAsDate(Date date)
    {
        writeValue(DateUtils.formatDate(date));
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsDouble(java.lang.Double)
     */
    public void writeValueAsDouble(Double d)
    {
        writeValue(d.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsLong(java.lang.Long)
     */
    public void writeValueAsLong(Long l)
    {
        writeValue(l.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsFloat(java.lang.Float)
     */
    public void writeValueAsFloat(Float f)
    {
        writeValue(f.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsBoolean(boolean)
     */
    public void writeValueAsBoolean(boolean b)
    {
        writeValue(b ? "true" : "false");
    }
}
