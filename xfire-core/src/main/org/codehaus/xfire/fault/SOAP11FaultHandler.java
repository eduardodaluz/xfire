package org.codehaus.xfire.fault;

import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.util.DOMStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates a fault message based on an exception for SOAP 1.2 messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SOAP11FaultHandler
	implements FaultHandler
{
    public static final String NAME = "1.1";
    
    /**
     * @see org.codehaus.xfire.fault.FaultHandler#handleFault(java.lang.Exception, org.codehaus.xfire.MessageContext)
     */
    public void handleFault( Exception e, 
                             MessageContext context )
    {
        XFireFault fault = createFault(e);

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer;
        try
        {
            writer = factory.createXMLStreamWriter( context.getResponseStream() );
            writer.writeStartDocument();
            writer.writeStartElement("soap:Envelope");
            writer.writeAttribute("xmlns:soap", SOAPConstants.SOAP11_ENVELOPE_NS);
            
            Map namespaces = fault.getNamespaces();
            for ( Iterator itr = namespaces.keySet().iterator(); itr.hasNext(); )
            {
                String prefix = (String) itr.next();
                writer.writeAttribute("xmlns:"+prefix, (String) namespaces.get(prefix));
            }
            
            writer.writeStartElement("soap:Body");
            writer.writeStartElement("soap:Fault");

            writer.writeStartElement("faultcode");
            
            String codeString = fault.getCode();
            if ( codeString.equals( XFireFault.RECEIVER ) )
            {
                codeString = "Server";
            }
            if ( codeString.equals( XFireFault.SENDER ) )
            {
                codeString = "Server";
            }
            else if ( codeString.equals( XFireFault.DATA_ENCODING_UNKNOWN ) )
            {
                codeString = "Client";
            }
            
            writer.writeCharacters( codeString );
            writer.writeEndElement();
            
            writer.writeStartElement("faultstring");
            writer.writeCharacters( fault.getMessage() );
            writer.writeEndElement();

            if ( fault.hasDetails() )
            {
                Element details = fault.getDetailElement();
                
                writer.writeStartElement("detail");
                
                NodeList children = details.getChildNodes();
                for ( int i = 0; i < children.getLength(); i++ )
                {
                    Node n = children.item(i);
                    if ( n instanceof Element )
                    {
                        DOMStreamWriter.writeElement((Element)n, writer);
                    }
                }
                
                writer.writeEndElement(); // Details
            }
            
            writer.writeEndElement(); // Fault
            writer.writeEndElement(); // Body
            writer.writeEndElement(); // Envelope
            writer.writeEndDocument(); 
            writer.close();
        }
        catch (XMLStreamException xe)
        {
            throw new XFireRuntimeException("Couldn't create fault.", xe);
        }
    }

	/**
     * @param detailElement
     * @param writer
     */
    private void STAXStreamDOMWriter(Element detailElement, XMLStreamWriter writer)
    {
        // TODO Auto-generated method stub
        
    }

    /**
	 * @param e
	 * @return
	 */
	private XFireFault createFault(Exception e)
	{
		XFireFault fault = XFireFault.createFault(e);
        
		return fault;
	}

}
