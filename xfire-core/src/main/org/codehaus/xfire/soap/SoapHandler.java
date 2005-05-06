package org.codehaus.xfire.soap;

import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.EndpointHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.stax.StaxSerializer;

/**
 * Processes SOAP invocations. The process is as follows: <ul> <li>Read in Headers to a DOM tree</li> <li>Check "role"
 * and MustUnderstand attributes for validity</li> <li>Invoke the request HandlerPipeline</li> <li>Invoke the service
 * EndpointHandler</li> <li>Invoke the response HandlerPipeline</li> <li>Invoke <code>writeResponse</code> on the
 * EndpointHandler</li> </ul>
 * <p/>
 * TODO: outline what happens when a fault occurrs.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 28, 2004
 */
public class SoapHandler
        extends AbstractHandler
{
    private static final String HANDLER_STACK = "xfire.handlerStack";

    public SoapHandler()
    {
    }

    /**
     * Invoke the Header and Body Handlers for the SOAP message.
     */
    public void invoke(MessageContext context)
            throws Exception
    {
        XMLStreamReader reader = context.getXMLStreamReader();
        String encoding = null;

        Stack handlerStack = new Stack();
        context.setProperty(HANDLER_STACK, handlerStack);

        EndpointHandler bodyHandler = (EndpointHandler) context.getService().getBinding();
        
        boolean end = false;
        while (!end && reader.hasNext())
        {
            int event = reader.next();
            switch (event)
            {
                case XMLStreamReader.START_DOCUMENT:
                    encoding = reader.getCharacterEncodingScheme();
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
                        validateHeaders(context);
                    }
                    else if (reader.getLocalName().equals("Body"))
                    {
                        createResponseHeader(context);
                        invokeRequestPipeline(handlerStack, context);

                        handlerStack.push(bodyHandler);
                        bodyHandler.invoke(context);
                    }
                    else if (reader.getLocalName().equals("Envelope"))
                    {
                        context.setSoapVersion(reader.getNamespaceURI());
                    }
                    break;
                default:
                    break;
            }
        }

        if (bodyHandler.hasResponse(context) && context.getReplyDestination() != null)
        {
            writeResponse(context, encoding, handlerStack, bodyHandler);
        }
    }

    /**
     * Create a response envelope and write the response message.
     *
     * @param context
     * @param encoding
     * @param handlerStack
     * @throws Exception
     */
    protected void writeResponse(MessageContext context, 
                                 String encoding, 
                                 Stack handlerStack,
                                 EndpointHandler bodyHandler)
            throws Exception
    {
        Attachments atts = (Attachments) context.getProperty(Attachments.ATTACHMENTS_KEY);
        if (atts != null && atts.size() > 0)
        {
            createMimeOutputStream(context);
        }

        XMLStreamWriter writer = createResponseWriter(context, encoding);

        writeHeaders(context, writer);
        invokeResponsePipeline(handlerStack, context);

        QName body = context.getSoapVersion().getBody();
        writer.writeStartElement(body.getPrefix(),
                body.getLocalPart(),
                body.getNamespaceURI());

        bodyHandler.writeResponse(context);

        writer.writeEndDocument();

        writer.close();
    }

    private void createMimeOutputStream(MessageContext context)
    {
        // TODO No outgoing attachment support yet.
    }

    public void handleFault(XFireFault fault, MessageContext context)
    {
        Stack handlerStack = (Stack) context.getProperty(HANDLER_STACK);

        while (!handlerStack.empty())
        {
            Handler handler = (Handler) handlerStack.pop();
            handler.handleFault(fault, context);
        }
    }

    protected void invokeRequestPipeline(Stack handlerStack, MessageContext context)
            throws Exception
    {
        if (context.getTransport() != null)
            invokePipeline(context.getTransport().getRequestPipeline(), handlerStack, context);

        if (context.getService() != null)
            invokePipeline(context.getService().getRequestPipeline(), handlerStack, context);
    }

    protected void invokeResponsePipeline(Stack handlerStack, MessageContext context)
            throws Exception
    {
        if (context.getService() != null)
            invokePipeline(context.getService().getResponsePipeline(), handlerStack, context);

        if (context.getTransport() != null)
            invokePipeline(context.getTransport().getResponsePipeline(), handlerStack, context);
    }

    protected void invokePipeline(HandlerPipeline pipeline,
                                  Stack handlerStack,
                                  MessageContext context)
            throws Exception
    {
        if (pipeline != null)
        {
            handlerStack.push(pipeline);
            pipeline.invoke(context);
        }
    }

    private XMLStreamWriter createResponseWriter(MessageContext context,
                                                 String encoding)
            throws XMLStreamException
    {
        XMLStreamWriter writer = getXMLStreamWriter(context);

        if (encoding == null)
            writer.writeStartDocument("UTF-8", "1.0");
        else
            writer.writeStartDocument(encoding, "1.0");

        QName env = context.getSoapVersion().getEnvelope();
        writer.setPrefix(env.getPrefix(), env.getNamespaceURI());
        writer.writeStartElement(env.getPrefix(),
                env.getLocalPart(),
                env.getNamespaceURI());
        writer.writeNamespace(env.getPrefix(), env.getNamespaceURI());

        return writer;
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

        Element header = builder.buildElement(null, context.getXMLStreamReader());

        context.setRequestHeader(header);
    }

    protected void createResponseHeader(MessageContext context)
    {
        QName headerQ = context.getSoapVersion().getHeader();
        Element response = new Element(headerQ.getPrefix() + ":" + headerQ.getLocalPart(),
                headerQ.getNamespaceURI());

        context.setResponseHeader(response);
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
        if (context.getRequestHeader() == null)
            return;

        SoapVersion version = context.getSoapVersion();
        Elements elements = context.getRequestHeader().getChildElements();
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
        ServiceEndpoint endpoint = context.getService();
        if (endpoint.getRequestPipeline() != null &&
                understands(endpoint.getRequestPipeline(), name))
            return;

        if (endpoint.getResponsePipeline() != null &&
                understands(endpoint.getResponsePipeline(), name))
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

    protected void writeHeaders(MessageContext context, XMLStreamWriter writer)
            throws XMLStreamException
    {
        Element e = context.getResponseHeader();
        if (e != null && e.getChildCount() > 0)
        {
            StaxSerializer ser = new StaxSerializer();

            ser.writeElement(e, writer);
        }
    }
}
