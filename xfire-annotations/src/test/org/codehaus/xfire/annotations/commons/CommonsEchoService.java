package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.EchoService;

/**
 * @author Arjen Poutsma
 * @@WebService(name = "EchoService", targetNamespace = "http://www.openuri.org/2004/04/HelloWorld")
 * @@org.codehaus.xfire.annotations.commons.soap.SOAPBinding(style = 1)
 */
public class CommonsEchoService
        implements EchoService
{

    /**
     * Returns the input.
     *
     * @param input the input.
     * @return the input.
     * @@WebMethod(operationName = "echoString", action="urn:EchoString")
     * @@Oneway()
     * @@.input WebParam("echoParam")
     * @@.return WebResult("echoResult")
     */
    public String echo(String input)
    {
        return input;
    }
}
