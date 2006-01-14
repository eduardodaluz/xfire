package org.codehaus.xfire.util.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.handler.SoapSerializerHandler;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

/**
 * Creates a org.w3c.dom.Document for the outgoing message and sets the
 * outgoing message's serializer to a DOMSerializer.
 * <p>
 * To access the DOM simply do:
 * <pre>
 * OutMessage msg = context.getOutMessage();
 * Document doc = (Document) msg.getProperty(DOMOutHandler.DOM_MESSAGE);
 * </pre>
 */
public class DOMOutHandler
    extends AbstractHandler
{
    public static final String DOM_MESSAGE = "dom.message";
    
    public DOMOutHandler()
    {
        super();
        setPhase(Phase.POST_INVOKE);
        after(SoapSerializerHandler.class.getName());
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        OutMessage message = context.getOutMessage();
        ByteArrayInputStream inStream = new ByteArrayInputStream(getMessageBytes(message, context));
        Document doc = DOMUtils.readXml(inStream);
        
        message.setProperty(DOM_MESSAGE, doc);

        message.setSerializer(new DOMSerializer(doc));
    }

    private byte[] getMessageBytes(OutMessage message, MessageContext context)
        throws Exception
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XMLStreamWriter byteArrayWriter = factory.createXMLStreamWriter(outputStream);
        message.getSerializer().writeMessage(message, byteArrayWriter, context);
        byteArrayWriter.flush();
        return outputStream.toByteArray();
    }
}
