/**
 * 
 */
package org.codehaus.xfire.jaxb2;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.codehaus.xfire.jaxb2.POJOTest.Account;

@WebService(endpointInterface="org.codehaus.xfire.jaxb2.AccountService", serviceName="AccountService")
public class AccountServiceImpl implements AccountService
{
    public Account getAccount()
    {
        return new Account();
    }
    
    public String auth(@WebParam(header=true) String password, String text)
    {
        return text;
    }
}