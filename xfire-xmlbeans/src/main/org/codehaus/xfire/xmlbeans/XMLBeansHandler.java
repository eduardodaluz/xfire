package org.codehaus.xfire.xmlbeans;

import javax.xml.stream.XMLStreamReader;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.util.STAXUtils;

/**
 * An XMLBeans handler which can be used in SOAP and REST services.
 * Override handleObject and getResponseObject to add in your service
 * logic.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 31, 2004
 */
public abstract class XMLBeansHandler
    extends AbstractHandler
{
    public abstract void handleObject( XmlObject request, MessageContext context ) 
        throws Exception;
    
    public abstract XmlObject getResponseObject( MessageContext context ) 
        throws Exception;

    public void invoke(MessageContext context, XMLStreamReader reader) throws Exception
    {
        XmlObject request = XmlObject.Factory.parse(reader);
        handleObject( request, context );
        
        XmlObject response = getResponseObject(context);
        if ( response != null )
        {
            STAXUtils.copy(response.newXMLStreamReader(), getXMLStreamWriter(context));
        }
    }
}
