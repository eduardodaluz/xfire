package org.codehaus.xfire.service.bridge;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.binding.ObjectService;
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
        ObjectService service = (ObjectService) context.getService();
        
        AbstractMessageBridge bridge;
        if (service.getStyle().equals(SoapConstants.STYLE_WRAPPED) 
            && service.getUse().equals(SoapConstants.USE_LITERAL))
        {
            bridge = new WrappedBridge(context);
        }
        else if (service.getStyle().equals(SoapConstants.STYLE_DOCUMENT) 
                 && service.getUse().equals(SoapConstants.USE_LITERAL))
        {
            bridge = new DocumentBridge(context);
        }
        else if (service.getStyle().equals(SoapConstants.STYLE_RPC) 
                 && service.getUse().equals(SoapConstants.USE_ENCODED))
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
