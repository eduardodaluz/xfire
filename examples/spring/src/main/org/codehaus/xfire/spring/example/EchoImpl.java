// START SNIPPET: echo
package org.codehaus.xfire.spring.example;

/**
 * Provides a default implementation of the echo service interface.
 */
public class EchoImpl
        implements Echo
{
    public String echo(String in)
    {
        return in;
    }

}
// END SNIPPET: echo