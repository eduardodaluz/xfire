
package jsr181.jaxb.auth;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import xfire.authenticate.fault.AuthenticationFault;

@WebService(serviceName = "AuthService", targetNamespace = "urn:xfire:auth-fault", endpointInterface = "jsr181.jaxb.auth.AuthServicePortType")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class AuthServiceCustomImpl
    implements AuthServicePortType
{


    public String authenticate(String in0, String in1)
        throws AuthenticationFault_Exception
    {
        AuthenticationFault fault = new AuthenticationFault();
        fault.setErrorCode(1);
        
        throw new AuthenticationFault_Exception("Fault!", fault);
    }

}
