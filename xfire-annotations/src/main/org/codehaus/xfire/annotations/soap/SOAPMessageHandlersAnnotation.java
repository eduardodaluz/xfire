package org.codehaus.xfire.annotations.soap;

import java.io.Serializable;

/**
 * Represents a common implementation of the SOAP message handlers annotation. Specifies a list of {@link
 * SOAPMessageHandler SOAP protocol handlers} that run before and after business methods on the Web Service. These
 * handlers are called in response to SOAP messages targeting the service. The <code>SOAPMessageHandlersAnnotation</code>
 * is an array of SOAPMessageHandler types. The handlers are run in the order in which they appear in the annotation,
 * starting with the first handler in the array.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class SOAPMessageHandlersAnnotation implements Serializable
{
    private SOAPMessageHandler[] value;

    /**
     * Initializes a new instance of the <code>SOAPMessageHandlersAnnotation</code>.
     *
     * @param value the handlers.
     */
    public SOAPMessageHandlersAnnotation(SOAPMessageHandler[] value)
    {
        this.value = value;
    }

    /**
     * Returns the SOAP message handlers.
     *
     * @return the message handlers.
     */
    public SOAPMessageHandler[] getValue()
    {
        return value;
    }
}
