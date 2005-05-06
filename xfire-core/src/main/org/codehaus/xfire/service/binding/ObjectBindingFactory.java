package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.soap.SoapConstants;

/**
 * Create a MessageReaders and MessageWriters for a ObjectService.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public class ObjectBindingFactory
{
	public static ObjectBinding getMessageBinding(String style, String use)
    {
        if (style.equals(SoapConstants.STYLE_WRAPPED) 
            && use.equals(SoapConstants.USE_LITERAL))
        {
            return new WrappedBinding();
        }
        else if (style.equals(SoapConstants.STYLE_DOCUMENT) 
                 && use.equals(SoapConstants.USE_LITERAL))
        {
            return new DocumentBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) 
                 && use.equals(SoapConstants.USE_ENCODED))
        {
            return new RPCEncodedBinding();
        }
        else if (style.equals(SoapConstants.STYLE_MESSAGE) 
                && use.equals(SoapConstants.USE_LITERAL))
       {
           return new MessageBinding();
       }
        else
        {
        	throw new UnsupportedOperationException( "Service style/use not supported." );
        }
	}
}
