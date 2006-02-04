package org.codehaus.xfire.fault;

public interface AuthService
{
    public String authenticate( String username, String password ) throws AuthenticationFault_Exception;
}
