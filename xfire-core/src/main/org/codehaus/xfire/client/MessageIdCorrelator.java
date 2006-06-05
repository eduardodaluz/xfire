package org.codehaus.xfire.client;

import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;

/**
 * Correlates a response message by the message id.
 * 
 * @author Dan Diephouse
 */
public class MessageIdCorrelator implements Correlator
{
    public ClientCall correlate(MessageContext context, List calls)
    {
        if (context.getId() == null) return null;
        
        for (Iterator itr = calls.iterator(); itr.hasNext();)
        {
            ClientCall call = (ClientCall) itr.next();
            
            if (call.getContext().getId() != null &&
                call.getContext().getId().equals(context.getId()))
            {
                return call;
            }
        }
        
        return null;
    }
}