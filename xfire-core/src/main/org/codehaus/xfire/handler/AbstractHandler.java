package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public abstract class AbstractHandler
    extends AbstractXFireComponent
    implements Handler
{
    private static final Object STAX_WRITER_KEY = "stax-writer";

    /**
     * Returns null by default, indicating that no headers
     * were understood.
     * 
     * @see org.codehaus.xfire.handler.Handler#getUnderstoodHeaders()
     */
    public QName[] getUnderstoodHeaders()
    {
        return null;
    }

    /**
     * Locates the fault handler on the service to write
     * the fault to the response message.
     * 
     * @see org.codehaus.xfire.handler.Handler#handleFault(org.codehaus.xfire.fault.XFireFault, org.codehaus.xfire.MessageContext)
     */
    public void handleFault( Exception e, MessageContext context )
    {
        Service service = context.getService();
        
        service.getFaultHandler().handleFault( e, context );
    }
    
    public XMLStreamWriter getXMLStreamWriter( MessageContext context )
    {
        XMLStreamWriter writer = (XMLStreamWriter) context.getProperty(STAX_WRITER_KEY);
        
        if ( writer == null )
        {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            try
            {
                writer = factory.createXMLStreamWriter(context.getResponseStream());
            }
            catch (XMLStreamException e)
            {
                throw new XFireRuntimeException("Couldn't create STAX writer.", e);
            }
            
            context.setProperty(STAX_WRITER_KEY, writer);
        }
        
        return writer;
    }
}
