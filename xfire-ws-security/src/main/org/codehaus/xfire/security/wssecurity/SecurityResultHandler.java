package org.codehaus.xfire.security.wssecurity;

import java.util.Vector;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.codehaus.xfire.security.SecurityResult;

public class SecurityResultHandler
{
    SecurityResult result = new SecurityResult();

    public SecurityResultHandler()
    {

    }

    public SecurityResultHandler(SecurityResult res)
    {
        result = res;
    }

    /**
     * @param wsResult
     * @return
     */
    public SecurityResult process(Vector wsResult)
    {

        for (int i = 0; i < wsResult.size(); i++)
        {
            WSSecurityEngineResult r = (WSSecurityEngineResult) wsResult.get(i);
            if (r.getAction() == WSConstants.UT)
            {
                handleUsernameToken(r, result);
            }
            else
            {
                if (r.getAction() == WSConstants.TS)
                {
                    handleTimeStamp(r, result);
                }
            }
        }
        return result;
    }

    private void handleTimeStamp(WSSecurityEngineResult r, SecurityResult result)
    {
        result.setTsCreated(r.getTimestamp().getCreated());
        result.setTsExpire(r.getTimestamp().getExpires());

    }

    /**
     * @param userToken
     * @param result
     */
    private void handleUsernameToken(WSSecurityEngineResult wsRes, SecurityResult result)
    {
        WSUsernameTokenPrincipal principal =(WSUsernameTokenPrincipal) wsRes.getPrincipal(); 
        result.setUser(principal.getName());
        result.setPassword(principal.getPassword());
        result.setPasswordHashed(WSConstants.PASSWORD_DIGEST.equals(principal.getPasswordType()));
        

    }

}
