package org.codehaus.xfire.fault;

import org.codehaus.xfire.MessageContext;

/**
 * A fault handler takes an exception and generates
 * a fault message which is written to the response message.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface FaultHandler
{
    String ROLE = FaultHandler.class.getName();

    /**
     * Take an exception and generate a fault message 
     * which is written to the response OutputStream.
     * 
     * TODO: Get the correct outputstream if we are using WS-Addressing
     * 
     * @param e
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context );
}
