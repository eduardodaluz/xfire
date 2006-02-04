package org.codehaus.xfire.fault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AuthServiceImpl implements AuthService
{
    public String authenticate( String username, String password ) throws AuthenticationFault_Exception
    {
        AuthenticationFault fault = new AuthenticationFault();
        fault.setErrorCode(1);
        fault.setMessage("Invalid username/password");
        throw new AuthenticationFault_Exception("message", fault);
    }
}
