package org.codehaus.xfire.security.handlers;

import java.util.Map;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.SecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityConstants;
import org.codehaus.xfire.security.SecurityProcessor;
import org.codehaus.xfire.security.wssecurity.OutSecurityDefaultBuilder;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class OutSecurityHandler
    extends SecurityHandler
{

    public OutSecurityHandler()
    {
        after(DOMOutHandler.class.getName());
        builder = new OutSecurityDefaultBuilder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        super.invoke(context);
        
        propagateContextData(context);
        OutMessage message = context.getOutMessage();
        message.setSerializer(new OutSecuritySerializer(message.getSerializer(),
                (OutSecurityProcessor) getProcessor()));

    }

    /**
     * @param context
     */
    private void propagateContextData(MessageContext context)
    {
        String userName = (String) context
                .getProperty(SecurityConstants.SECURITY_OUT_USER_NAME_CONTEXT_KEY);
        String userPass = (String) context
                .getProperty(SecurityConstants.SECURITY_OUT_USER_PASS_CONTEXT_KEY);
        OutSecurityProcessor processor = (OutSecurityProcessor) getProcessor();
        processor.setUsername(userName);
        processor.setUserPassword(userPass);

    }

}
