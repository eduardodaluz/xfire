package org.codehaus.xfire.security.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class OutSecuritySerializer
    implements MessageSerializer
{
    private static final Log LOG = LogFactory.getLog(OutSecuritySerializer.class);

    private MessageSerializer serializer;

    private OutSecurityProcessor processor;

    public OutSecuritySerializer(MessageSerializer serializer, OutSecurityProcessor processor)
    {

    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        throw new UnsupportedOperationException();

    }

    private byte[] getMessageBytes(OutMessage message, MessageContext context)
        throws Exception
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XMLStreamWriter byteArrayWriter = factory.createXMLStreamWriter(outputStream);
        serializer.writeMessage(message, byteArrayWriter, context);
        byteArrayWriter.flush();
        return outputStream.toByteArray();
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            ByteArrayInputStream inStream = new ByteArrayInputStream(getMessageBytes(message,
                                                                                     context));
            Document doc = DOMUtils.readXml(inStream);
            doc = processor.process(doc);
            org.codehaus.xfire.util.stax.DOMStreamWriterHelper.write(writer, doc);
            inStream.close();
            writer.close();

        }
        catch (XMLStreamException e)
        {
            LOG.error(e);
            throw XFireFault.createFault(e);
        }
        catch (Exception e)
        {
            LOG.error(e);
            throw XFireFault.createFault(e);
        }

    }

}
