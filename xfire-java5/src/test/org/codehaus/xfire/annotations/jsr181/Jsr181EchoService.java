/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.xfire.annotations.jsr181;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * @author Arjen Poutsma
 */
@WebService(name = "EchoService", targetNamespace = "http://www.openuri.org/2004/04/HelloWorld")
public class Jsr181EchoService
{
    @WebMethod(operationName = "echoString", action = "urn:EchoString")
    @WebResult(name = "echoResult")
    @Oneway
    public String echo(@WebParam(name = "echoParam", header = true) String input)
    {
        return input;
    }
}
