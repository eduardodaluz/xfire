package org.codehaus.xfire.handler.dom;

import org.codehaus.xfire.MessageContext;
import org.dom4j.Node;

/** 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 18, 2004
 */
public class EchoDOMHandler
    extends AbstractDOMHandler
{
    /**
     * @see org.codehaus.xfire.handler.dom.DOMHandler#invoke(org.codehaus.xfire.MessageContext, org.codehaus.xfire.handler.dom.Message, org.codehaus.xfire.handler.dom.Message)
     */
    public void invoke( MessageContext context, 
                        Message request, 
                        Message response ) 
        throws Exception
    {
        response.getHeader().add( (Node) request.getHeader().clone() );
        response.getBody().add( (Node) request.getBody().clone() );
    }
}
