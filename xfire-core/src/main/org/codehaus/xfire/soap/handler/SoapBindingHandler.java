package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.DispatchServiceHandler;
import org.codehaus.xfire.handler.LocateBindingHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.DocumentBinding;
import org.codehaus.xfire.service.binding.MessageBinding;
import org.codehaus.xfire.service.binding.RPCBinding;
import org.codehaus.xfire.service.binding.WrappedBinding;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Takes the SoapBinding from the MessageContext, selects an appropriate MessageSerailizer
 * and reads in the message.
 * @author Dan Diephouse
 */
public class SoapBindingHandler
    extends AbstractHandler
{   
    public SoapBindingHandler()
    {
        super();
        
        after(LocateBindingHandler.class.getName());
        before(DispatchServiceHandler.class.getName());
    }

    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        SoapBinding binding = (SoapBinding) context.getBinding();
        
        AbstractBinding ser = getSerializer(binding.getStyle(), binding.getUse());

        ser.readMessage(context.getInMessage(), context);
        
        if (!ser.isClientModeOn(context))
        {
            context.getInPipeline().addHandler(ser);
        }
    }

    public static AbstractBinding getSerializer(String style, String use)
    {
        if (style.equals(SoapConstants.STYLE_WRAPPED) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new WrappedBinding();
        }
        else if (style.equals(SoapConstants.STYLE_DOCUMENT)
                && use.equals(SoapConstants.USE_LITERAL))
        {
            return new DocumentBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_ENCODED))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_MESSAGE) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new MessageBinding();
        }
        else
        {
            throw new UnsupportedOperationException("Service style/use not supported: " + style
                    + "/" + use);
        }
    }
}