package org.codehaus.xfire.annotations;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * Thrown when an exception occurs related to XFire's annotation support.
 *
 * @author Arjen Poutsma
 */
public class AnnotationException
        extends XFireRuntimeException
{
    /**
     * Initializes a new <code>AnnotationException</code> with the given string message.
     *
     * @param message the message.
     */
    public AnnotationException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new <code>AnnotationException</code> with the given string message and cause.
     *
     * @param message the message.
     * @param cause   the cause.
     */
    public AnnotationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
