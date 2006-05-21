package org.codehaus.xfire.demo;

import java.security.cert.X509Certificate;
import java.util.Vector;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;

import sun.security.x509.X500Name;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
//START SNIPPET: secresult

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
                int action  = secRes.getAction();
                // USER TOKEN
                if( (action &  WSConstants.UT )>0   ){
                WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) secRes
                        .getPrincipal();
                // Set user property to user from UT to allow response encryption
                context.setProperty(WSHandlerConstants.ENCRYPTION_USER,principal.getName());
                System.out.print("User : " + principal.getName() + " password : "
                        + principal.getPassword() + "\n");
                }
                // SIGNATURE
                if( ( action & WSConstants.SIGN ) > 0 ){
                    X509Certificate cert = secRes.getCertificate();
                    X500Name principal = (X500Name) secRes.getPrincipal();
                    // Do something whith cert
                    System.out.print("Signature for : "  + principal.getCommonName());
                }
            }
        }
    }
}
//  END SNIPPET: secresult
