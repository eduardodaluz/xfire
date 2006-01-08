/*
 * Created on Dec 22, 2005
 */
package org.codehaus.xfire.security;

import org.codehaus.xfire.MessageContext;

import junit.framework.TestCase;

public class Tester
    extends TestCase
{

    public void testTest()
    {
        UsernamePasswordEchoService echoUsernamePasswordService = new UsernamePasswordEchoServiceImpl();
        MessageContext messageContext = new MessageContext();
        messageContext.setProperty(SecurityConstants.SECURITY_IN_USER_NAME_CONTEXT_KEY , "brian");
        messageContext.setProperty(SecurityConstants.SECURITY_IN_USER_PASS_CONTEXT_KEY, "bonner");
        System.out.println(echoUsernamePasswordService.echo("test", messageContext));
    }
}
