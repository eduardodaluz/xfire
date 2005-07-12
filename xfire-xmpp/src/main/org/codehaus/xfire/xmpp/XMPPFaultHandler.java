package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jivesoftware.smack.packet.XMPPError;

/**
 * Creates fault information for the response packet.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPFaultHandler
    extends AbstractHandler
{
    public static final String XMPP_ERROR = "xfire.xmppError";

    /**
     * @param e
     * @param context
     * @throws Exception
     */
    public void invoke(MessageContext context)
    {
        /**
         * From the JEP-0076 spec:
         * _SOAP Error_             _HTTP Code_  _IQ Error Description_
         * env:VersionMismatch      500          <internal-server-error/>
         * env:MustUnderstand       500          <internal-server-error/>
         * env:Sender               400          <bad-request/>
         * env:Receiver             500          <internal-server-error/>
         * env:DataEncodingUnknown  500          <internal-server-error/>
         */

        XFireFault fault = (XFireFault) context.getExchange().getFaultMessage().getBody();
        XMPPError error = null;
        if (fault.getFaultCode().equals(XFireFault.SENDER))
        {
            error = new XMPPError(400);
        }
        else
        {
            error = new XMPPError(500);
        }
        
        context.setProperty(XMPP_ERROR, error);
    }
}
