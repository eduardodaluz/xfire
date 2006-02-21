package org.codehaus.xfire.message.wrapped;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoWithFaultImpl implements EchoWithFault
{
    public String echo( String echo ) throws EchoFault
    {
        EchoFault fault = new EchoFault("message");
        fault.setCustomMessage(echo);
        throw fault;
    }
}
