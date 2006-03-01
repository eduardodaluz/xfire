package org.codehaus.xfire.security.handlers;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.security.SecurityConstants;
import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.security.wssecurity.InSecurityDefaultBuilder;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class InSecurityHandler  extends SecurityHandler
{

    /**
     * 
     */
    public InSecurityHandler()
    {

        before(ReadHeadersHandler.class.getName());
        after(DOMInHandler.class.getName());
        setPhase(Phase.PARSE);
        builder = new InSecurityDefaultBuilder();
    }

    /**
     * Returns the unencrypted document on the inMessage Sets two properties on
     * the XFire Message context: 'user' and 'password' to values supplied in
     * the WS-Security header
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        super.invoke(context);
        Document doc = (Document) context.getCurrentMessage().getProperty(DOMInHandler.DOM_MESSAGE);
        OutputStream os = new FileOutputStream("inhandler.xml");
        
       // DOM2Writer.serializeAsXML(doc.getDocumentElement(), new OutputStreamWriter(os), false);
        
        os.close();
        
        SecurityResult result = getProcessor().process(doc);
        doc = result.getDocument();

        context.setProperty(SecurityConstants.SECURITY_IN_USER_NAME_CONTEXT_KEY, result.getUser());
        context.setProperty(SecurityConstants.SECURITY_IN_USER_PASS_CONTEXT_KEY, result
                .getPassword());

        context.getInMessage().setXMLStreamReader(new W3CDOMStreamReader(doc.getDocumentElement()));
        context.setProperty(DOMInHandler.DOM_MESSAGE, doc);
    }

}
