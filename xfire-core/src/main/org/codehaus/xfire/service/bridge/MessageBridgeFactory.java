package org.codehaus.xfire.service.bridge;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Create a MessageReaders and MessageWriters for a ObjectService.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public class MessageBridgeFactory
{
	public static MessageBridge createMessageBridge(MessageContext context)
    {
        ServiceEndpoint endpoint = context.getService();
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        
        AbstractMessageBridge bridge;
        if (binding.getStyle().equals(SoapConstants.STYLE_WRAPPED) 
            && binding.getUse().equals(SoapConstants.USE_LITERAL))
        {
            bridge = new WrappedBridge(context);
        }
        else if (binding.getStyle().equals(SoapConstants.STYLE_DOCUMENT) 
                 && binding.getUse().equals(SoapConstants.USE_LITERAL))
        {
            bridge = new DocumentBridge(context);
        }
        else if (binding.getStyle().equals(SoapConstants.STYLE_RPC) 
                 && binding.getUse().equals(SoapConstants.USE_ENCODED))
        {
            bridge = new RPCEncodedBridge(context);
        }
        else
        {
        	throw new UnsupportedOperationException( "Service style/use not supported." );
        }
        
        return bridge;
	}
}
