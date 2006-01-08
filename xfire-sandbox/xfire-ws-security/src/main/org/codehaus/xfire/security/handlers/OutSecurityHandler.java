package org.codehaus.xfire.security.handlers;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.SecurityConstants;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class OutSecurityHandler
    extends AbstractHandler
{
    private OutSecurityProcessor processor;

    public OutSecurityProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(OutSecurityProcessor processor)
    {
        this.processor = processor;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        propagateContextData(context);
        OutMessage message = context.getOutMessage();
        message.setSerializer(new OutSecuritySerializer(message.getSerializer(), getProcessor()));

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
        OutSecurityProcessor processor = getProcessor();
        processor.setUsername(userName);
        processor.setUserPassword(userPass);

    }

}
