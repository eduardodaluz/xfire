package org.codehaus.xfire.handler.dom;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;

/**
 * A handler in a DOM processed pipeline. This does not
 * inherit from the <code>Handler</code> class.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 18, 2004
 */
public interface DOMHandler
{
    String ROLE = DOMHandler.class.getName();

    /**
     * @return null or an empty array if there are no headers.
     */
    QName[] getUnderstoodHeaders();
    
    /**
     * Invoke a handler. If a fault occurs it will be handled
     * via the <code>handleFault</code> method.
     * 
     * @param message The message context.
     */
    void invoke( MessageContext context,
                 Message request,
                 Message response ) throws Exception;
}
