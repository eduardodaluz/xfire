package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * A handler which echoes the SOAP Body back.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class BadHandler
    extends AbstractHandler
    implements EndpointHandler
{
    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke( MessageContext context ) 
        throws Exception
    {
        throw new Exception("Bad handler!");
    }

    /**
     * @see org.codehaus.xfire.handler.EndpointHandler#writeResponse(org.codehaus.xfire.MessageContext)
     * @param context
     * @throws XFireFault
     */
    public void writeResponse(MessageContext context)
        throws XFireFault
    {
    }
}
