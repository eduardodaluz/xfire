package org.codehaus.xfire.security.wss4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.DOMUtils.NullResolver;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.w3c.dom.Document;

/**
 * Ensures that the signature round trip process works.
 */
public class WSS4JInOutTest
    extends AbstractSecurityTest
{

    public WSS4JInOutTest()
    {
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
        ctx.setProperty(WSHandlerConstants.USER, "myAlias");
        ctx.setProperty("password", "myAliasPassword");
        ctx.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        
        handler.invoke(ctx);
        printNode(doc);
        assertValid(doc, "//wsse:Security");
        assertValid(doc, "//wsse:Security/ds:Signature");
       
        byte[] docbytes = getMessageBytes(doc);
        XMLStreamReader reader = STAXUtils.createXMLStreamReader(new ByteArrayInputStream(docbytes), null, null);
  
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());
        doc = STAXUtils.read(db, reader, false);
        
        WSS4JInHandler inHandler = new WSS4JInHandler();

        InMessage inmsg = new InMessage();
        inmsg.setProperty(DOMInHandler.DOM_MESSAGE, doc);

        ctx.setCurrentMessage(inmsg);
        inHandler.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
        inHandler.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/insecurity.properties");

        inHandler.invoke(ctx);
    }
    
    private byte[] getMessageBytes(Document doc)
        throws Exception
    {
        //XMLOutputFactory factory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        //XMLStreamWriter byteArrayWriter = factory.createXMLStreamWriter(outputStream);
        XMLStreamWriter byteArrayWriter = STAXUtils.createXMLStreamWriter(outputStream, null, null);
        
        STAXUtils.writeDocument(doc, byteArrayWriter, false);
        
        byteArrayWriter.flush();
        return outputStream.toByteArray();
    }
}
