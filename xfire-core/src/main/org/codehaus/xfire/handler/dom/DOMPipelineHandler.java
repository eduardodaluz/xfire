package org.codehaus.xfire.handler.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.util.STAXStreamReader;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

/**
 * Creates a DOM structure from the stream (lazily instantiated).
 * It then passes off the DOM messages to a series of 
 * <code>DOMHandler</code>s.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 18, 2004
 */
public class DOMPipelineHandler
    extends AbstractHandler
{
    private List handlers;
    
    public DOMPipelineHandler()
    {
        this.handlers = new ArrayList();
    }

    public DOMPipelineHandler( List handlers )
    {
        this.handlers = handlers;
    }
    
    public QName[] getUnderstoodHeaders()
    {
        return super.getUnderstoodHeaders();
    }
    
    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext, javax.xml.stream.XMLStreamReader)
     */
    public void invoke( MessageContext context, 
                        XMLStreamReader parser ) throws Exception
    {
        STAXStreamReader reader = new STAXStreamReader();
        
        Document docRequest = reader.readDocument(parser);
        Message request = new Message(docRequest);
        context.setSoapVersion( request.getSoapVersion() );
        
        Message response = new Message(request.getSoapVersion());
        
        for ( Iterator itr = getHandlers().iterator(); itr.hasNext(); )
        {
            DOMHandler handler = (DOMHandler) itr.next();
            
            handler.invoke( context, request, response );
        }
        
        XMLWriter writer = new XMLWriter(context.getResponseStream());
        writer.write(response.getMessage());
    }
    
    public List getHandlers()
    {
        return handlers;
    }
}
