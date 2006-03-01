package org.codehaus.xfire.security.handlers;

import java.util.Map;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.SecurityProcessor;
import org.codehaus.xfire.security.SecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.w3c.dom.Document;

public class SecurityHandler
    extends AbstractHandler
{

    protected SecurityProcessor processor;

    protected SecurityProcessorBuilder builder ;

    protected Map configuration;

    protected boolean configured;

    
    public void invoke(MessageContext context)
        throws Exception
    {
        if (!configured)
        {
            configureProcessor();
            configured = true;
        }
       
    }
    
    public SecurityProcessorBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(SecurityProcessorBuilder builder)
    {
        this.builder = builder;
    }

    public Map getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Map configuration)
    {
        this.configuration = configuration;
    }
    
    public SecurityProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(SecurityProcessor processor)
    {
        this.processor = processor;
    }
    
    public void configureProcessor()
    {
        if (configuration == null)
        {
            throw new RuntimeException("No configuration source specified");
        }
        builder.setConfiguration(configuration);
        builder.build(processor);
    }


}
