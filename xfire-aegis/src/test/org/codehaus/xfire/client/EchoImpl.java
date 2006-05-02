package org.codehaus.xfire.client;

import org.codehaus.xfire.aegis.Holder;

public class EchoImpl implements Echo
{
    public String echo(String text, Holder textAgain) {
        textAgain.setValue("header2");
        return text;
    }
}
