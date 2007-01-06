
package org.codehaus.xfire.generator.any;

import javax.jws.WebService;

import c.b.a._2006._07.authentication.AuthenticationInterface;

@WebService(serviceName = "AuthenticationService", targetNamespace = "http://a.b.c/2006/07/Authentication", endpointInterface = "c.b.a._2006._07.authentication.AuthenticationInterface")
public class AuthenticationServiceImpl
    implements AuthenticationInterface
{

    public Object login(String userName, String password) {
        if (userName.equals("dan")) {
        	return "hello";
        }
        
        else return new FooType();
    }

}
