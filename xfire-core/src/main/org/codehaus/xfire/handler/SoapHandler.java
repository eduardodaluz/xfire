package org.codehaus.xfire.handler;

import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
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
                    
                    try
                    {
                        bodyHandler.invoke(context);
                        handlerStack.push(bodyHandler);
                    }
                    catch(Exception e)
                    {
                        handleFault(e, context, handlerStack);
                    }
                }
                else if ( reader.getLocalName().equals("Envelope") )
                {
                    // create the response envelope when we have the startElement() info
                    writer = createResponseWriter(context, reader, encoding);
                }
                break;
            default:
                break;
            }
        }

        /* TODO
         * Invoke global and service Response Pipelines
         * Handle faults correctly
         */ 
        writeHeaders(context, writer);
        
        try
        {
            invokeResponsePipeline(handlerStack, context);
            
            writer.writeStartElement("soap", "Body", context.getSoapVersion().getNamespace());
            bodyHandler.writeResponse(context);
            writer.writeEndElement();
            
            writer.writeEndElement();  // Envelope

            writer.writeEndDocument();
            writer.close();
        }
        catch(Exception e)
        {
            handleFault(e, context, handlerStack);
        }        
    }

    private void handleFault(Exception e, MessageContext context, Stack handlerStack) 
        throws Exception
    {
        bodyHandler.handleFault(e, context);
        
        while(!handlerStack.empty())
        {
            Handler handler = (Handler) handlerStack.pop();
            handler.handleFault(e, context);
        }
        
        invokePipeline( context.getTransport().getFaultPipeline(), handlerStack, context );
        invokePipeline( context.getService().getFaultPipeline(), handlerStack, context );
        
        throw e;
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
        if (context.getTransport() != null )
            invokePipeline( context.getTransport().getResponsePipeline(), handlerStack, context );
        
        if (context.getService() != null )
            invokePipeline( context.getService().getResponsePipeline(), handlerStack, context );
    }

    protected void invokePipeline(HandlerPipeline pipeline, 
                                  Stack handlerStack, 
                                  MessageContext context)
        throws Exception
    {
        if ( pipeline != null )
        {
            pipeline.invoke(context);
            handlerStack.push(pipeline);    
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
                                                 XMLStreamReader reader,
                                                 String encoding)
        throws XMLStreamException, XFireFault
    {
        XMLStreamWriter writer = getXMLStreamWriter(context);
        if ( encoding == null )
            writer.writeStartDocument("UTF-8", "1.0");
        else
            writer.writeStartDocument(encoding, "1.0");
        
        String soapVersion = reader.getNamespaceURI();
        context.setSoapVersion(soapVersion);

        if ( context.getSoapVersion() == null )
            throw new XFireFault("SOAP version not recognized.", XFireFault.SENDER);
        
        String soapNS = context.getSoapVersion().getNamespace();
        writer.setPrefix("soap", soapNS);
        writer.writeStartElement("soap", "Envelope", soapNS);
        writer.writeNamespace("soap", soapNS);
                
        return writer;
    }
}
