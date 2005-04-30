package org.codehaus.xfire.wsdl;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * Thrown when an error occurs when attempting to create a WSDL from a <code>ServiceEndpoint</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLCreationException
        extends XFireRuntimeException
{
    /**
     * Creates a new <code>WSDLCreationException</code>.
     *
     * @param message the message.
     */
    public WSDLCreationException(String message)
    {
        super(message);
    }

    /**
     * Creates a new <code>WSDLCreationException</code>.
     *
     * @param message the message.
     * @param cause   the wrapped cause.
     */
    public WSDLCreationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
