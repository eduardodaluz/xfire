package org.codehaus.xfire.transport.jms;

public class BadEcho
{
    public String echo(String in)
        throws Exception
    {
        throw new Exception("BAD");
    }
}
