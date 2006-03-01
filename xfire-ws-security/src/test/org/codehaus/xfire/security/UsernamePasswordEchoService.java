/*
 * Created on Dec 22, 2005
 */
package org.codehaus.xfire.security;

import org.codehaus.xfire.MessageContext;

public interface UsernamePasswordEchoService
{
    /**
     * echoes the username and password back to the user in an xml document
     * 
     * @param inDocument
     * @param messageContext (contains the username and password)
     * @return  xml document to return
     */
    String echo(String text, MessageContext messageContext);
}
