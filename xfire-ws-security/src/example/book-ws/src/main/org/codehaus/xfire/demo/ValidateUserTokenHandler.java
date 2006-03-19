package org.codehaus.xfire.demo;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;

public class ValidateUserTokenHandler
    extends AbstractHandler
{

    public void invoke(MessageContext context)
        throws Exception
    {
        /*String user = (String) context
                .getProperty(SecurityConstants.SECURITY_IN_USER_NAME_CONTEXT_KEY);
        String password = (String) context
                .getProperty(SecurityConstants.SECURITY_IN_USER_PASS_CONTEXT_KEY);

        System.out.print("User : " + user + " pass : " + password);*/
    }

}
