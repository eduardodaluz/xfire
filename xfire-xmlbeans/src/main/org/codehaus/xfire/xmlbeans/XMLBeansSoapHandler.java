package org.codehaus.xfire.xmlbeans;

import javax.xml.stream.XMLStreamReader;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Handler;

/**
 * Processes an XML document using XMLBeans
 * 
 * @version $Revision$
 */
public class XMLBeansSoapHandler
    extends AbstractHandler
    implements Handler
{
    private XmlObject object;
    private XmlOptions options;
    
    private XMLBeansHandler bodyHandler;
    private XMLBeansHandler headerHandler;
    
    public XMLBeansSoapHandler(XMLBeansHandler bodyHandler, XMLBeansHandler headerHandler)
    {
        options = new XmlOptions();
        options.put( XmlOptions.SAVE_NO_XML_DECL, Boolean.TRUE );
        
        this.bodyHandler = bodyHandler;
        this.headerHandler = headerHandler;
    }

    public void invoke( MessageContext context, XMLStreamReader reader ) throws Exception
    {
        /*
        while ( true )
        {
            int event = reader.next();
            switch( event )
            {
                case XMLStreamReader.END_DOCUMENT:
                    return;
                case XMLStreamReader.START_ELEMENT:
                    if( reader.getLocalName().equals("Header") && headerHandler != null )
                    {
                        reader.nextTag();
                        headerHandler.handleObject(XmlObject.Factory.parse( reader ), context);
                    }
                    else if ( reader.getLocalName().equals("Body") )
                    {
                        reader.nextTag();
                        bodyHandler.handleObject(XmlObject.Factory.parse( reader ), context);
                    }
                    break;
                default:
                    break;
            }
        }
        
        XMLStreamWriter writer = getXMLStreamWriter(context);
        
        String soapVersion = null;
        writer.writeStartDocument(getEncoding(), "1.0");

        writer.setPrefix("soap", soapVersion);
        writer.writeStartElement("soap", "Envelope", soapVersion);
        writer.writeNamespace("soap", soapVersion);
        
        if ( headerHandler != null )
        {
            writer.writeStartElement("soap", "Header", soapVersion);
            headerHandler.getResponseObject(context);
            writer.writeEndElement();
        }
        
        writer.writeStartElement("soap", "Body", soapVersion);
        
        bodyHandler.getResponseObject(context);
        
        writer.writeEndElement(); // Body

        writer.writeEndElement();  // Envelope

        writer.writeEndDocument();
        writer.close();*/
    }

    /**
     * @return
     */
    private String getEncoding()
    {
        // TODO Auto-generated method stub
        return null;
    }
}