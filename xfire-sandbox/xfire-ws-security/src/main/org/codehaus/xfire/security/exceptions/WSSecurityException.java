package org.codehaus.xfire.security.exceptions;

public class WSSecurityException
    extends RuntimeException
{

    private int errorCode;
    
    public WSSecurityException(String msg, int code){
        super(msg);
        errorCode = code;
    }
    
}
