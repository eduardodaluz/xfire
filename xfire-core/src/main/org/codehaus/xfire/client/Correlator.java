package org.codehaus.xfire.client;

import java.util.List;

import org.codehaus.xfire.MessageContext;

public interface Correlator
{
    public Invocation correlate(MessageContext context, List invocations);
}
