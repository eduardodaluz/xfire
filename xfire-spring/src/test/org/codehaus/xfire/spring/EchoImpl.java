package org.codehaus.xfire.spring;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 25, 2005
 */
public class EchoImpl
    implements Echo
{
    public String echo( String echo )
    {
        return echo;
    }
}
