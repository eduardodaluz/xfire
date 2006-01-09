package org.codehaus.xfire.inout;

import org.codehaus.xfire.aegis.Holder;

public class MultipleOutServiceImpl implements MultipleOutService
{
    public String echo(String text, Holder in1, Holder in2)
    {
        in1.setValue("hi");
        in2.setValue("header");
        return text;
    }
}
