package org.codehaus.xfire.annotations.backport175;

import org.codehaus.xfire.annotations.EchoService;

/**
 * @author Arjen Poutsma
 * @org.codehaus.xfire.annotations.backport175.WebService(name = "EchoService", targetNamespace =
 * "http://www.openuri.org/2004/04/HelloWorld")
 * @org.codehaus.xfire.annotations.backport175.soap.SOAPBinding(style = 1, use=0, parameterStyle=1)
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
     * @org.codehaus.xfire.annotations.backport175.Oneway
     * @org.codehaus.xfire.annotations.backport175.WebResult(name="echoResult")
     */
    public String echo(String input)
    {
        return input;
    }
}
