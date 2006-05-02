package org.codehaus.xfire.client;

import org.codehaus.xfire.aegis.Holder;

public interface Echo
{
    public String echo(String text, Holder textAgain);
}
