
package org.codehaus.xfire.jaxws.services;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import services.auth.AuthServicePortType;
import xfire.authenticate.fault.AuthenticationFault;

@WebService(serviceName = "AuthService", targetNamespace = "urn:xfire:authenticate", endpointInterface = "services.auth.AuthServicePortType")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class AuthServiceImpl
    implements AuthServicePortType
{


    public String authenticate(String in0, String in1)
        throws services.auth.AuthenticationFault
    {
        AuthenticationFault fault = new AuthenticationFault();
        fault.setErrorCode(1);
        
        throw new services.auth.AuthenticationFault("fault", fault);
    }

}
