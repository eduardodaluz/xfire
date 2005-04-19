package org.codehaus.xfire.aegis;

import java.util.Calendar;
import java.util.Date;

import org.codehaus.xfire.util.DateUtils;

/**
 * Basic type conversions for reading messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractMessageReader
    implements MessageReader
{
    public AbstractMessageReader()
    {
    }
    
    /**
     * @see org.codehaus.xfire.aegis.MessageReader#getValueAsInt()
     */
    public int getValueAsInt()
    {
        return Integer.parseInt( getValue() );
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageReader#getValueAsDate()
     */
    public Date getValueAsDate()
    {
        if ( getValue() == null )
            return null;
        
        return DateUtils.parseDate( getValue() );
    }
    
    /**
     * @see org.codehaus.xfire.aegis.MessageReader#getValueAsDateTime()
     */
    public Date getValueAsDateTime()
    {
        if ( getValue() == null )
            return null;

        return DateUtils.parseDateTime( getValue() );
    }

    /**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsCalendar()
	 */
    public Calendar getValueAsCalendar()
    {
        if ( getValue() == null )
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( DateUtils.parseDateTime(getValue()) );
        return calendar;
    }

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsLong()
	 */
	public long getValueAsLong()
	{
        return Long.parseLong( getValue() );
	}
    
	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsDouble()
	 */
	public double getValueAsDouble()
	{
		return Double.parseDouble( getValue() );
	}

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsFloat()
	 */
	public float getValueAsFloat()
	{
		return Float.parseFloat( getValue() );
	}

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsBoolean()
	 */
	public boolean getValueAsBoolean()
	{
		return Boolean.valueOf( getValue() ).booleanValue();
	}
}
