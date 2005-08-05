// START SNIPPET: echo
package org.codehaus.xfire.spring.example;

/**
 * Provides the service contract for the echo service.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public interface Echo
{
    String echo(String in);
}
// END SNIPPET: echo