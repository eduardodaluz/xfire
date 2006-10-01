package org.codehaus.xfire.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class Echo
{
    @WebMethod
    public String echo(String text) throws EchoFault
    {
        throw new EchoFault();
    }
}
