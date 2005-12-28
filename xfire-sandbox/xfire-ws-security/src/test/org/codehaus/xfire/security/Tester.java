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
        messageContext.setProperty("username", "brian");
        messageContext.setProperty("password", "bonner");
        System.out.println(echoUsernamePasswordService.echo("test", messageContext));
    }
}
