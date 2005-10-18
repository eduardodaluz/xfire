package org.codehaus.xfire.fault;

import org.codehaus.xfire.fault.XFireFault;

public class CustomXFireFault
    extends XFireFault
{
    public CustomXFireFault()
    {
        super("CustomFault", XFireFault.MUST_UNDERSTAND);
    }
}