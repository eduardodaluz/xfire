package org.codehaus.xfire.aegis;

import org.codehaus.xfire.XFireRuntimeException;

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

    public boolean hasValue()
    {
        return getValue() != null;
    }
    
    /**
     * @see org.codehaus.xfire.aegis.MessageReader#getValueAsInt()
     */
    public int getValueAsInt()
    {
        if (getValue() == null) return 0;
        
        return Integer.parseInt( getValue() );
    }

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsLong()
	 */
	public long getValueAsLong()
	{
        if (getValue() == null) return 0l;
        
        return Long.parseLong( getValue() );
	}
    
	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsDouble()
	 */
	public double getValueAsDouble()
	{
        if (getValue() == null) return 0d;
        
        return Double.parseDouble( getValue() );
	}

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsFloat()
	 */
	public float getValueAsFloat()
	{
        if (getValue() == null) return 0f;
        
        return Float.parseFloat( getValue() );
	}

	/**
	 * @see org.codehaus.xfire.aegis.MessageReader#getValueAsBoolean()
	 */
	public boolean getValueAsBoolean()
	{
        String value = getValue();
        if (value == null) return false;
        
        if ("true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value))
            return true;

        if ("false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value))
            return false;
        
        throw new XFireRuntimeException("Invalid boolean value: " + value);
	}
}
