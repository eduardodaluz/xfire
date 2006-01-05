/**
 * 
 */
package org.codehaus.xfire.annotations.jsr181;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.codehaus.xfire.annotations.jsr181.HeaderServiceTest.UserToken;

@WebService(name="HeaderService", targetNamespace="urn:HeaderService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class HeaderService
{
    static String a;
    static String b;
    static String header;
    static UserToken authHeader;
    
    @WebMethod
    public void doSomething(@WebParam(name="a") String a,
                            @WebParam(name="header", header=true) String header,
                            @WebParam(name="b") String b) 
    {
        HeaderService.a = a;
        HeaderService.b = b;
        HeaderService.header = header;
    }
    
    @WebMethod
    public void doSomethingAuthenticated(@WebParam(name="UserToken", header=true) UserToken authHeader) 
    {
        HeaderService.authHeader = authHeader;
    }
    
    @WebMethod
    public void doSomethingAuthenticated2(@WebParam(name="UserToken", header=true) UserToken authHeader) 
    {
        HeaderService.authHeader = authHeader;
    }
}