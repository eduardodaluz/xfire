package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 25, 2004
 */
public class EndpointTestHandler
    extends AbstractHandler
    implements EndpointHandler
{

    public void writeResponse(MessageContext context)
        throws XFireFault
    {
        XMLStreamWriter writer = getXMLStreamWriter(context);
        try
        {
            writer.writeStartElement("urn:Test", "test");
            writer.writeCharacters("test");
            writer.writeEndElement();
            writer.flush();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't write response.", e, XFireFault.RECEIVER);
        }
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        // TODO Auto-generated method stub
        
    }
}
