package org.codehaus.xfire.message.wrapped;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoWithFaultImpl implements EchoWithFault
{
    public String echo( String echo ) throws EchoFault
    {
        throw new EchoFault(echo);
    }
}
