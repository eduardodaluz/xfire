package org.codehaus.xfire;

/**
 * Used for internal XFire exceptions when a fault shouldn't be returned to the service invoker.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireRuntimeException
        extends RuntimeException
{
    /**
     * Constructs a new xfire runtime exception with <code>null</code> as its detail message.
     */
    public XFireRuntimeException()
    {
    }

    /**
     * Constructs a new xfire runtime exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public XFireRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new xfire runtime exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public XFireRuntimeException(String message, Throwable cause)
    {
        super(message + ": " + cause.toString());
    }

    public XFireRuntimeException(Throwable cause)
    {
        super(cause == null ? null : cause.toString());
    }
}
