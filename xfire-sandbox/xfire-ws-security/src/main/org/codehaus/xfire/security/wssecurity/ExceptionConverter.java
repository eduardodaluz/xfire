package org.codehaus.xfire.security.wssecurity;

import org.codehaus.xfire.security.exceptions.WSSecurityException;

public class ExceptionConverter
{
    public static WSSecurityException convert(org.apache.ws.security.WSSecurityException ex)
    {
        WSSecurityException exc = new WSSecurityException(ex.getMessage(), ex.getErrorCode());
        return exc;

    }
}
