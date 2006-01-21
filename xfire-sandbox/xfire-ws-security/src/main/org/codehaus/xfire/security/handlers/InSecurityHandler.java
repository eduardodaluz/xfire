package org.codehaus.xfire.security.handlers;

import java.util.Map;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.InSecurityProcessorBuilder;
import org.codehaus.xfire.security.InSecurityResult;
import org.codehaus.xfire.security.SecurityConstants;
import org.codehaus.xfire.security.wssecurity.InSecurityDefaultBuilder;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class InSecurityHandler
    extends AbstractHandler
{

    private InSecurityProcessor processor;

    private InSecurityProcessorBuilder builder = new InSecurityDefaultBuilder();

    private Map configuration;

    private boolean configured;

    /**
     * 
     */
    public InSecurityHandler()
    {
        before(ReadHeadersHandler.class.getName());
        after(DOMInHandler.class.getName());
        setPhase(Phase.PARSE);
    }

    /**
     * Returns the unencrypted document on the inMessage Sets two properties on
     * the XFire Message context: 'user' and 'password' to values supplied in
     * the WS-Security header
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        if (!configured)
        {
            configureProcessor();
            configured = true;
        }
        Document doc = (Document) context.getCurrentMessage().getProperty(DOMInHandler.DOM_MESSAGE);
        InSecurityResult result = getProcessor().process(doc);
        doc = result.getDocument();

        context.setProperty(SecurityConstants.SECURITY_IN_USER_NAME_CONTEXT_KEY, result.getUser());
        context.setProperty(SecurityConstants.SECURITY_IN_USER_PASS_CONTEXT_KEY, result
                .getPassword());

        context.getInMessage().setXMLStreamReader(new W3CDOMStreamReader(doc.getDocumentElement()));
        context.setProperty(DOMInHandler.DOM_MESSAGE, doc);
    }

    public InSecurityProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(InSecurityProcessor processor)
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

    public InSecurityProcessorBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(InSecurityProcessorBuilder builder)
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

}
