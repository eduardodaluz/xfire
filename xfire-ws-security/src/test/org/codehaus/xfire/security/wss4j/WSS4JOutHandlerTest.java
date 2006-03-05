package org.codehaus.xfire.security.wss4j;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutHandlerTest
    extends AbstractSecurityTest
{    
    public void testUsernameToken()
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");
    
        WSS4JOutHandler handler = new WSS4JOutHandler();
    
        MessageContext ctx = new MessageContext();
    
        OutMessage msg = new OutMessage("");
        msg.setProperty(DOMOutHandler.DOM_MESSAGE, doc);
    
        ctx.setCurrentMessage(msg);
        ctx.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        ctx.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        ctx.setProperty(WSHandlerConstants.USER, "username");
        ctx.setProperty("password", "userSecretePass");
        handler.invoke(ctx);
        
        assertValid(doc, "//wsse:Security");
        assertValid(doc, "//wsse:Security/wsse:UsernameToken");
        assertValid(doc, "//wsse:Security/wsse:UsernameToken/wsse:Username[text()='username']");
    }
    
    public void testEncrypt()
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");
    
        WSS4JOutHandler handler = new WSS4JOutHandler();
    
        MessageContext ctx = new MessageContext();
    
        OutMessage msg = new OutMessage("");
        msg.setProperty(DOMOutHandler.DOM_MESSAGE, doc);
    
        ctx.setCurrentMessage(msg);
        ctx.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
        ctx.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        ctx.setProperty(WSHandlerConstants.ENC_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        ctx.setProperty(WSHandlerConstants.USER, "alias");
        ctx.setProperty("password", "aliaspass");
        ctx.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        
        handler.invoke(ctx);
        
        assertValid(doc, "//wsse:Security");
        assertValid(doc, "//s:Body/xenc:EncryptedData");
    }
    
    public void testSignature()
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");
    
        WSS4JOutHandler handler = new WSS4JOutHandler();
    
        MessageContext ctx = new MessageContext();
    
        OutMessage msg = new OutMessage("");
        msg.setProperty(DOMOutHandler.DOM_MESSAGE, doc);
    
        ctx.setCurrentMessage(msg);
        ctx.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
        ctx.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        ctx.setProperty(WSHandlerConstants.USER, "alias");
        ctx.setProperty("password", "aliaspass");
        ctx.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        
        handler.invoke(ctx);
        
        assertValid(doc, "//wsse:Security");
        assertValid(doc, "//wsse:Security/ds:Signature");
    }
    

    public void testTimestamp()
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");
    
        WSS4JOutHandler handler = new WSS4JOutHandler();
    
        MessageContext ctx = new MessageContext();
    
        OutMessage msg = new OutMessage("");
        msg.setProperty(DOMOutHandler.DOM_MESSAGE, doc);
    
        ctx.setCurrentMessage(msg);
        handler.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP);
        handler.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        handler.setProperty(WSHandlerConstants.USER, "alias");
        handler.setProperty("password", "aliaspass");
        handler.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        
        handler.invoke(ctx);
        
        assertValid(doc, "//wsse:Security");
        assertValid(doc, "//wsse:Security/wsu:Timestamp");
    }
}
