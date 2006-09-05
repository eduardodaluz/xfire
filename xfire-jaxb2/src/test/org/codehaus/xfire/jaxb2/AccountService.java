/**
 * 
 */
package org.codehaus.xfire.jaxb2;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.codehaus.xfire.jaxb2.POJOTest.Account;

@WebService
public interface AccountService
{
    public Account getAccount();
    
    public String auth(@WebParam(header=true) String password, String text);
}