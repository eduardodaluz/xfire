package org.codehaus.xfire;

/**
 * Used for internal XFire exceptions when a fault shouldn't be returned
 * to the service invoker.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireRuntimeException
    extends RuntimeException
{
    /**
	 * @param string
	 * @param t
	 */
	public XFireRuntimeException(String message, Throwable t)
	{
		super( message, t );
	}

    /**
     * @param string
     */
    public XFireRuntimeException(String message)
    {
        super( message );
    }
}
