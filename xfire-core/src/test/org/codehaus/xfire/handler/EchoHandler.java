package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;

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
        XMLStreamWriter writer = null;
        try
        {
            writer = getXMLStreamWriter(context);
            
            boolean done = false;
            int attribute = 0;
            int namespace = 0;
            
            while ( !done )
            {
                int event = parser.next();
                switch( event )
                {
                    case XMLStreamConstants.START_DOCUMENT:
                        writer.writeStartDocument();
                        break;
                    case XMLStreamConstants.END_DOCUMENT:            
                        writer.writeEndDocument();
                        done = true;
                        break;
                    case XMLStreamConstants.ATTRIBUTE:
                        break;
                    case XMLStreamConstants.NAMESPACE:
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        writer.writeStartElement( 
                                parser.getPrefix(),
                                parser.getLocalName(),
                                parser.getNamespaceURI());

                        for ( int i = 0; i < parser.getAttributeCount(); i++ )
                        {
                            writer.writeAttribute(
                                    parser.getAttributeNamespace(i),
                                    parser.getAttributeLocalName(i),
                                    parser.getAttributeValue(i));
                        }
                        
                        for ( int i = 0; i < parser.getAttributeCount(); i++ )
                        {
                            writer.writeNamespace(
                                parser.getNamespacePrefix(i),
                                parser.getNamespaceURI(i));
                        }
                        
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        writer.writeEndElement();
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        writer.writeCharacters( parser.getText() );  
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Error processing document.", e);
        }
        finally
        {
            if ( writer != null )
            {
                try
                {
                    writer.close();
                    context.getResponseStream().flush();
                }
                catch (Exception e)
                {
                    throw new XFireRuntimeException("Error closing stream.", e);
                }
            }
            
        }
    }
}
