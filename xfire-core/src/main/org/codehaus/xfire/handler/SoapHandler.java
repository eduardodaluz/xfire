package org.codehaus.xfire.handler;

import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Processes SOAP invocations. The process is as follows:
 * <ul>
 * <li>Read in Headers to a DOM tree</li>
 * <li>Check "role" and MustUnderstand attributes for validity</li>
 * <li>Invoke the request HandlerPipeline</li>
 * <li>Invoke the service EndpointHandler</li>
 * <li>Invoke the response HandlerPipeline</li>
 * <li>Invoke <code>writeResponse</code> on the EndpointHandler</li>
 * </ul>
 * 
 * TODO: outline what happens when a fault occurrs.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 28, 2004
 */
public class SoapHandler 
    extends AbstractHandler
{
    public static final String REQUEST_HEADER_KEY = "xfire.request-header";
    public static final String RESPONSE_HEADER_KEY = "xfire.response-header";

    private EndpointHandler bodyHandler;
    private Handler headerHandler;
    private static final String HANDLER_STACK = "xfire.handlerStack";

    public SoapHandler( EndpointHandler bodyHandler )
    {
        this.bodyHandler = bodyHandler;
    }

    /**
     * Invoke the Header and Body Handlers for the SOAP message.
     */
    public void invoke(MessageContext context)
    	throws Exception
    {
        XMLStreamReader reader = context.getXMLStreamReader();
        XMLStreamWriter writer = null;
        String encoding = null;

        Stack handlerStack = new Stack();
        context.setProperty(HANDLER_STACK, handlerStack);

        boolean end = false;
        while ( !end && reader.hasNext() )
        {
            int event = reader.next();
            switch( event )
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
                if( reader.getLocalName().equals("Header") && headerHandler != null )
                {
                    readHeaders(context);
                    validateHeaders(context);
                }
                else if ( reader.getLocalName().equals("Body") )
                {
                    invokeRequestPipeline(handlerStack, context);

                    handlerStack.push(bodyHandler);
                    bodyHandler.invoke(context);
                }
                else if ( reader.getLocalName().equals("Envelope") )
                {
                    context.setSoapVersion(reader.getNamespaceURI());
                }
                break;
            default:
                break;
            }
        }

        Attachments atts = (Attachments) context.getProperty(Attachments.ATTACHMENTS_KEY);
        if (atts != null && atts.size() > 0)
        {
            createMimeOutputStream(context);
        }
        
        writer = createResponseWriter(context, encoding);

        writeHeaders(context, writer);

        invokeResponsePipeline(handlerStack, context);

        QName body = context.getSoapVersion().getBody();
        writer.writeStartElement(body.getPrefix(), 
                                 body.getLocalPart(),
                                 body.getNamespaceURI());
        
        bodyHandler.writeResponse(context);
        writer.writeEndElement();
        
        writer.writeEndElement();  // Envelope

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
        if (context.getTransport() != null )
            invokePipeline( context.getTransport().getRequestPipeline(), handlerStack, context );

        if (context.getService() != null )
            invokePipeline( context.getService().getRequestPipeline(), handlerStack, context );
    }

    protected void invokeResponsePipeline(Stack handlerStack, MessageContext context) 
        throws Exception
    {
       if (context.getService() != null )
            invokePipeline( context.getService().getResponsePipeline(), handlerStack, context );

       if (context.getTransport() != null )
           invokePipeline( context.getTransport().getResponsePipeline(), handlerStack, context );
    }

    protected void invokePipeline(HandlerPipeline pipeline, 
                                  Stack handlerStack, 
                                  MessageContext context)
        throws Exception
    {
        if ( pipeline != null )
        {
            handlerStack.push(pipeline);    
            pipeline.invoke(context);
        }
    }

    protected void readHeaders( MessageContext context ) 
    	throws XMLStreamException
    {
        Document doc = DOMUtils.createDocument();
        Element e = doc.createElementNS(context.getSoapVersion().getNamespace(), "Header");
        
        STAXUtils.readElements(e, context.getXMLStreamReader());
            
        context.setProperty(REQUEST_HEADER_KEY, e);
    }
    
    protected void writeHeaders( MessageContext context, XMLStreamWriter writer ) 
    	throws XMLStreamException
    {
        Element e = (Element) context.getProperty(REQUEST_HEADER_KEY);
        if ( e != null )
        {
            writer.writeStartElement("soap", "Body", context.getSoapVersion().getNamespace());

            STAXUtils.writeElement(e, writer);
            
            writer.writeEndElement();
        }
    }

    protected void validateHeaders(MessageContext context)
    {
        /* TODO Check MustUnderstand and Role attributes
         */ 
    }
    
    private XMLStreamWriter createResponseWriter(MessageContext context, 
                                                 String encoding)
        throws XMLStreamException, XFireFault
    {
        XMLStreamWriter writer = getXMLStreamWriter(context);

        if ( encoding == null )
            writer.writeStartDocument("UTF-8", "1.0");
        else
            writer.writeStartDocument(encoding, "1.0");
        
        QName env  = context.getSoapVersion().getEnvelope();
        writer.setPrefix(env.getPrefix(), env.getNamespaceURI());
        writer.writeStartElement(env.getPrefix(), 
                                 env.getLocalPart(),
                                 env.getNamespaceURI());
        writer.writeNamespace(env.getPrefix(), env.getNamespaceURI());

        return writer;
    }
}
