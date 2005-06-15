package org.codehaus.xfire.aegis;

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
