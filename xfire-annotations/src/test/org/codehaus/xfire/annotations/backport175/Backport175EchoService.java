package org.codehaus.xfire.annotations.backport175;

import org.codehaus.xfire.annotations.EchoService;

/**
 * @author Arjen Poutsma
 * @org.codehaus.xfire.annotations.backport175.WebService(name = "EchoService", targetNamespace =
 * "http://www.openuri.org/2004/04/HelloWorld")
 */
public class Backport175EchoService
        implements EchoService
{
    /**
     * Returns the input.
     *
     * @param input the input.
     * @return the input.
     * @org.codehaus.xfire.annotations.backport175.WebMethod(operationName = "echoString", action="urn:EchoString")
     */
    public String echo(String input)
    {
        return input;
    }
}
