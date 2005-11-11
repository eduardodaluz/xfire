package org.codehaus.xfire.message.wrapped;

public interface EchoWithFault
{
    public String echo( String echo ) throws EchoFault, Throwable;
}
