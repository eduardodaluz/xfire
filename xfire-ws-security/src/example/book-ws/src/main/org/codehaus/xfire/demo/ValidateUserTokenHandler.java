package org.codehaus.xfire.demo;

import java.util.Vector;

import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class ValidateUserTokenHandler
    extends AbstractHandler
{

    public void invoke(MessageContext context)
        throws Exception
    {
        Vector result = (Vector) context.getProperty(WSHandlerConstants.RECV_RESULTS);
        for (int i = 0; i < result.size(); i++)
        {
            WSHandlerResult res = (WSHandlerResult) result.get(i);
            for (int j = 0; j < res.getResults().size(); j++)
            {
                WSSecurityEngineResult secRes = (WSSecurityEngineResult) res.getResults().get(j);
                WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) secRes
                        .getPrincipal();
                System.out.print("User : " + principal.getName() + " password : "
                        + principal.getPassword() + "\n");
            }
        }

    }

}
