package org.codehaus.xfire.xmpp;

public class BadEcho
{
    public String echo(String in)
        throws Exception
    {
        throw new Exception("BAD");
    }
}
