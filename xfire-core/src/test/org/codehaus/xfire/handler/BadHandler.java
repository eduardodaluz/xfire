package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamReader;
import org.codehaus.xfire.MessageContext;

/**
 * A handler which echoes the SOAP Body back.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class BadHandler
    extends AbstractHandler
{
    /**
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext, javax.xml.stream.XMLStreamReader)
     */
    public void invoke( MessageContext context, XMLStreamReader parser ) 
        throws Exception
    {
        throw new Exception("Bad handler!");
    }
}
