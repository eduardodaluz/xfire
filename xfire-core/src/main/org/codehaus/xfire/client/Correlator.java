package org.codehaus.xfire.client;

import java.util.List;

import org.codehaus.xfire.MessageContext;

public interface Correlator
{
    public ClientCall correlate(MessageContext context, List calls);
}
