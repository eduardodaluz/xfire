package org.codehaus.xfire.xmlbeans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.util.STAXUtils;

/**
 * An XMLBeans handler which can be used in SOAP and REST services.
 * Override handleRequest and getResponse to add in your service
 * logic.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 31, 2004
 */
public abstract class AbstractXMLBeansHandler
    extends AbstractHandler
{
    public abstract XmlObject[] invoke( XmlObject[] request, MessageContext context ) 
        throws Exception;

    public void invoke(MessageContext context, XMLStreamReader reader) throws Exception
    {
        List params = new ArrayList();
        
        int event = reader.getEventType();
        boolean end = false;
        while ( !end )
        {
            switch( event )
            {
            case XMLStreamReader.START_ELEMENT:
                XmlObject param = XmlObject.Factory.parse(reader);
                params.add(param);
                break;
            case XMLStreamReader.END_ELEMENT:
                end = true;
                break;
            case XMLStreamReader.END_DOCUMENT:
                end = true;
                break;
            default:
                break;
            }
            event = reader.next();
        }
        
        XmlObject[] response =
            invoke( (XmlObject[]) params.toArray(new XmlObject[params.size()]), context );

        if ( response != null )
        {
            for ( int i = 0; i < response.length; i++ )
            {
                STAXUtils.copy(response[i].newXMLStreamReader(), getXMLStreamWriter(context));
            }
        }
    }
}
