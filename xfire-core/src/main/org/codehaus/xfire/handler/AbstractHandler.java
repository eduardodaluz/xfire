package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public abstract class AbstractHandler
    extends AbstractXFireComponent
    implements Handler
{
    public static final String STAX_WRITER_KEY = "xfire.stax-writer";
    
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

    public String[] getRoles()
    {
        return null;
    }
    
    /**
     * @see org.codehaus.xfire.handler.Handler#handleFault(java.lang.Exception, org.codehaus.xfire.MessageContext)
     * @param e
     * @param context
     */
    public void handleFault( Exception e, MessageContext context )
    {
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
