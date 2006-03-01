package org.codehaus.xfire.security.exceptions;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSSecurityException
    extends RuntimeException
{

    private int errorCode;
    
    public WSSecurityException(String msg, int code){
        super(msg);
        errorCode = code;
    }

    public int getErrorCode()
    {
        return errorCode;
    }
    
}
