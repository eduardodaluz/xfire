package org.codehaus.xfire.services;

/**
 * Has a static method which shouldn't be registered with the service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class StaticEcho
{
    
    public static String echoStatic( String echo )
    {
        return echo;
    }
    
    public String echo( String echo )
    {
        return echo;
    }
}
