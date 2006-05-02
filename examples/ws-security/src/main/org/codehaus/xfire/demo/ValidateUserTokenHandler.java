package org.codehaus.xfire.demo;

import java.util.Vector;

import org.apache.ws.security.WSConstants;
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

 /*   public static final int NO_SECURITY = 0;
    public static final int UT = 0x1; // perform UsernameToken
    public static final int SIGN = 0x2; // Perform Signature
    public static final int ENCR = 0x4; // Perform Encryption
 */
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
                int action  = secRes.getAction();
                
                if( (action &  WSConstants.UT )>0   ){
                WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) secRes
                        .getPrincipal();
                context.setProperty(WSHandlerConstants.ENCRYPTION_USER,principal.getName());
                System.out.print("User : " + principal.getName() + " password : "
                        + principal.getPassword() + "\n");
                }
                
            }
        }

    }

}
