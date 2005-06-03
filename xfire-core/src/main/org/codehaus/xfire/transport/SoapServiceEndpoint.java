package org.codehaus.xfire.transport;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;

/**
 * A <code>ChannelEndpoint</code> which executes the in pipeline
 * on the service and starts a <code>MessageExchange</code>.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapServiceEndpoint
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(SoapServiceEndpoint.class);
 
    public void onReceive(MessageContext context, InMessage msg)
    {
        if (log.isDebugEnabled()) log.debug("Received message to " + msg.getUri());
        
        context.setInMessage(msg);
        
        try
        {
            readHeaders(msg, context);
        }
        catch (Exception e)
        {
            log.error("Couldn't parse soap message. Aborting receive.", e);
            return;
        }
        
        try
        {
            MessageSerializer binding = context.getService().getBinding();
            binding.readMessage(context.getInMessage(), context);
            
            finishReadingMessage(msg, context);
        }
        catch (Exception e)
        {
            log.error("Couldn't read message. Aborting receive.", e);
            return;
        }

        try
        {
           validateHeaders(context);
        }
        catch (XFireFault fault)
        {
            context.getExchange().handleFault(fault);
            return;
        }
        
        context.getExchange().doExchange();
    }

    public void readHeaders(InMessage message, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = message.getXMLStreamReader();
        try
        {
            boolean end = false;
            while (!end && reader.hasNext())
            {
                int event = reader.next();
                switch (event)
                {
                    case XMLStreamReader.START_DOCUMENT:
                        String encoding = reader.getCharacterEncodingScheme();
                        message.setEncoding(encoding);
                        break;
                    case XMLStreamReader.END_DOCUMENT:
                        end = true;
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        break;
                    case XMLStreamReader.START_ELEMENT:
                        if (reader.getLocalName().equals("Header"))
                        {
                            readHeaders(context);
                        }
                        else if (reader.getLocalName().equals("Body"))
                        {
                            return;
                        }
                        else if (reader.getLocalName().equals("Envelope"))
                        {
                            message.setSoapVersion(reader.getNamespaceURI());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse message.", e, XFireFault.SENDER);
        }
    }

    public void finishReadingMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = message.getXMLStreamReader();

        try
        {
            while (reader.hasNext()) reader.next();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse message.", e, XFireFault.SENDER);
        }
    }

    /**
     * Read in the headers as a YOM Element and create a response Header.
     *
     * @param context
     * @throws XMLStreamException
     */
    protected void readHeaders(MessageContext context)
            throws XMLStreamException
    {
        StaxBuilder builder = new StaxBuilder();

        Element header = builder.buildElement(null, context.getInMessage().getXMLStreamReader());

        context.getInMessage().setHeader(header);
    }

    /**
     * Validates that the mustUnderstand and role headers are processed correctly.
     *
     * @param context
     * @throws XFireFault
     */
    protected void validateHeaders(MessageContext context)
            throws XFireFault
    {
        if (context.getInMessage().getHeader() == null)
            return;

        SoapVersion version = context.getInMessage().getSoapVersion();
        Elements elements = context.getInMessage().getHeader().getChildElements();
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            String mustUnderstand = e.getAttributeValue("mustUnderstand",
                    version.getNamespace());

            if (mustUnderstand != null && mustUnderstand.equals("1"))
            {
                assertUnderstandsHeader(context, new QName(e.getNamespaceURI(), e.getLocalName()));
            }
        }
    }

    /**
     * Assert that a service understands a particular header.  If not, a fault is thrown.
     *
     * @param context
     * @param name
     * @throws XFireFault
     */
    protected void assertUnderstandsHeader(MessageContext context, QName name)
            throws XFireFault
    {
        Service endpoint = context.getService();
        if (endpoint.getInPipeline() != null &&
                understands(endpoint.getInPipeline(), name))
            return;

        if (endpoint.getOutPipeline() != null &&
                understands(endpoint.getOutPipeline(), name))
            return;

        throw new XFireFault("Header {" + name.getLocalPart() + "}" + name.getNamespaceURI()
                + " was not undertsood by the service.", XFireFault.MUST_UNDERSTAND);
    }

    /**
     * Determine if a particular pipeline undertands a header.
     *
     * @param pipeline
     * @param name
     * @return
     */
    private boolean understands(HandlerPipeline pipeline, QName name)
    {
        for (int i = 0; i < pipeline.size(); i++)
        {
            QName[] understoodQs = pipeline.getHandler(i).getUnderstoodHeaders();

            if (understoodQs != null)
            {
                for (int j = 0; j < understoodQs.length; j++)
                {
                    if (understoodQs[j].equals(name))
                        return true;
                }
            }
        }

        return false;
    }
}
