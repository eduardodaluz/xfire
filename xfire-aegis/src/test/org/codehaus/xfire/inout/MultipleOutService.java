package org.codehaus.xfire.inout;

import org.codehaus.xfire.aegis.Holder;

public interface MultipleOutService
{
    public String echo(String text, Holder in1, Holder in2);
}