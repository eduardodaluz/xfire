package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * A handler which echoes the SOAP Body back.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class EchoHandler
    extends AbstractHandler
{
    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext, javax.xml.stream.XMLStreamReader)
     */
    public void invoke( MessageContext context, XMLStreamReader parser ) 
        throws XFireFault
    {
        try
        {
            STAXUtils.copy(parser, getXMLStreamWriter(context));
            getXMLStreamWriter(context).flush();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse the request document.", e, XFireFault.SENDER);
        }
    }
}
