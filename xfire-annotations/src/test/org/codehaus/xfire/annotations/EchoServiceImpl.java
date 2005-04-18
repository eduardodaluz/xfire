package org.codehaus.xfire.annotations;

public class EchoServiceImpl
        implements EchoService
{
    public String echo(String input)
    {
        return input;
    }

    public void async()
    {
    }
}
