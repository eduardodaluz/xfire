package org.codehaus.xfire.service.assembler;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * Thrown when an exception occurs in the assembler package.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class AssemblerException
        extends XFireRuntimeException
{
    /**
     * Initializes a new <code>AssemblerException</code> with the given string message.
     *
     * @param message the message.
     */
    public AssemblerException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new <code>AssemblerException</code> with the given string message and cause.
     *
     * @param message the message.
     * @param cause   the cause.
     */
    public AssemblerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
