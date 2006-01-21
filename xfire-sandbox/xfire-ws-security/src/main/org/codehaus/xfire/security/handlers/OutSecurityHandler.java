package org.codehaus.xfire.security.handlers;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityConstants;
import org.codehaus.xfire.security.wssecurity.OutSecurityDefaultBuilder;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class OutSecurityHandler
    extends AbstractHandler
{
    private OutSecurityProcessor processor;

    private OutSecurityProcessorBuilder builder = new OutSecurityDefaultBuilder();

    private Map configuration;

    private boolean configured;
    
    public OutSecurityHandler()
    {
        after(DOMOutHandler.class.getName());
    }

    public OutSecurityProcessorBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(OutSecurityProcessorBuilder builder)
    {
        this.builder = builder;
    }

    public OutSecurityProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(OutSecurityProcessor processor)
    {
        this.processor = processor;
    }

    public void configureProcessor()
    {
        if (configuration == null)
        {
            throw new RuntimeException("Processor not configured");
        }
        builder.setConfiguration(configuration);
        builder.build(processor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        if( !configured ){
            configureProcessor();
            configured=true;
        }
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

    public Map getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Map configuration)
    {
        this.configuration = configuration;
    }

}
