package org.codehaus.xfire.services;

public class EchoOverloadImpl implements EchoOverload
{
    public String echo( String echo )
    {
        return echo;
    }
    
    public String echo( String echo, String echo2 )
    {
        return echo + echo2;
    }
}
