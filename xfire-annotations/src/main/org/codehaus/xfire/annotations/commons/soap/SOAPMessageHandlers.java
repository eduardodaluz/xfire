package org.codehaus.xfire.annotations.commons.soap;

import org.codehaus.xfire.annotations.soap.SOAPMessageHandler;
import org.codehaus.xfire.annotations.soap.SOAPMessageHandlersAnnotation;


/**
 * Commons Attributes version of the SOAP message handlers annotation.
 *
 * @author Arjen Poutsma
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.CLASS)
 * @see org.codehaus.xfire.annotations.soap.SOAPMessageHandlersAnnotation
 */
public class SOAPMessageHandlers
        extends SOAPMessageHandlersAnnotation
{
    /**
     * Initializes a new instance of the <code>SOAPMessageHandlersAnnotation</code>.
     *
     * @param value the handlers.
     */
    public SOAPMessageHandlers(SOAPMessageHandler[] value)
    {
        super(value);
    }
}
