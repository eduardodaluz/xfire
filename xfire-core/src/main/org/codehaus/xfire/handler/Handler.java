package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;

/**
 * <p>
 * A handler is just something that processes an XML message.
 * </p>
 * <p>
 * There is one handler per service. This can delegate to a bunch of
 * other handlers if need be.
 * </p>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface Handler
{
	String ROLE = Handler.class.getName();
    
    /**
     * @return null or an empty array if there are no headers.
     */
    QName[] getUnderstoodHeaders();
    
    /**
     * The roles which this service applies to. 
     * @return <code>null</code> or an empty if this endpoint handles no roles.
     */
    String[] getRoles();
    
    /**
     * Invoke a handler. If a fault occurs it will be handled
     * via the <code>handleFault</code> method.
     * @param message The message context.
     */
    void invoke( MessageContext context ) throws Exception;

    /**
     * Handles faults that occur in this handler. This is not
     * responsible for actually writing the fault response message.
     * 
     * @param context
     */
    void handleFault( Exception e, MessageContext context );
}
