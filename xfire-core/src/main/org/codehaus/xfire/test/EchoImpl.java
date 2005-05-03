package org.codehaus.xfire.test;

/**
 * Echo
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoImpl
        implements Echo
{
    public String echo(String echo)
    {
        return echo;
    }
}
