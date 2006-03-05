package org.codehaus.xfire.security.wss4j;

import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JInHandlerTest
    extends AbstractSecurityTest
{

    public WSS4JInHandlerTest()
    {
    }

    public void testSignature()
        throws Exception
    {
        Document doc = readDocument("signed.xml");

        WSS4JInHandler handler = new WSS4JInHandler();

        MessageContext ctx = new MessageContext();

        InMessage msg = new InMessage();
        msg.setProperty(DOMInHandler.DOM_MESSAGE, doc);

        ctx.setCurrentMessage(msg);
        handler.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
        handler.setProperty(WSHandlerConstants.SIG_PROP_FILE,
                        "META-INF/xfire/insecurity.properties");

        handler.setProperty(WSHandlerConstants.ACTOR, "actor1");

        handler.invoke(ctx);
    }
    
    public void testInvalidSignature()
        throws Exception
    {
        Document doc = readDocument("signed_invalid.xml");
    
        WSS4JInHandler handler = new WSS4JInHandler();
    
        MessageContext ctx = new MessageContext();
    
        InMessage msg = new InMessage();
        msg.setProperty(DOMInHandler.DOM_MESSAGE, doc);
    
        ctx.setCurrentMessage(msg);
        ctx.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
        ctx.setProperty(WSHandlerConstants.SIG_PROP_FILE,
                        "META-INF/xfire/insecurity.properties");
    
        handler.getProperties().put(WSHandlerConstants.ACTOR, "actor1");
    
        try
        {
            handler.invoke(ctx);
            fail("Signatuare was not supposed to be valid.");
        }
        catch (XFireFault fault)
        {
        }
        
    }

    public void testEncryption()
        throws Exception
    {
        Document doc = readDocument("in_enc.xml");

        WSS4JInHandler handler = new WSS4JInHandler();

        MessageContext ctx = new MessageContext();

        InMessage msg = new InMessage();
        msg.setProperty(DOMInHandler.DOM_MESSAGE, doc);

        ctx.setCurrentMessage(msg);
        ctx.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.SIGNATURE);
        ctx.setProperty(WSHandlerConstants.DEC_PROP_FILE, "META-INF/xfire/insecurity.properties");
        ctx.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/insecurity.properties");

        ctx.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, TestPwdCallback.class.getName());

        handler.invoke(ctx);

        printNode(doc);
    }

}
