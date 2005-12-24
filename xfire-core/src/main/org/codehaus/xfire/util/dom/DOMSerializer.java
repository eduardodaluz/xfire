package org.codehaus.xfire.util.dom;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 */
public class DOMSerializer
    implements MessageSerializer
{
    private static final Log LOG = LogFactory.getLog(DOMSerializer.class);

    private Document doc;

    public DOMSerializer(Document doc)
    {
        this.doc = doc;
    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        throw new UnsupportedOperationException();
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            STAXUtils.writeDocument(doc, writer);
            writer.flush();
        }
        catch (Exception e)
        {
            LOG.error(e);
            throw XFireFault.createFault(e);
        }
    }
}
