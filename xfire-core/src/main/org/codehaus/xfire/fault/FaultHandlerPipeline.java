package org.codehaus.xfire.fault;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultHandlerPipeline
    implements FaultHandler
{
    private List handlers;
    
    public FaultHandlerPipeline()
    {
        handlers = new ArrayList();
    }
    
    public FaultHandler getHandler(int i)
    {
        return (FaultHandler) handlers.get(i);
    }
    
    public int size()
    {
        return handlers.size();
    }

    public void handleFault(XFireFault fault, MessageContext context) 
    {
        for (Iterator itr = handlers.iterator(); itr.hasNext();)
        {
            FaultHandler h = (FaultHandler) itr.next();
            h.handleFault(fault, context);
        }
    }
    
    public void addHandler(FaultHandler handler)
    {
        handlers.add(handler);
    }
    
    public void remove(FaultHandler handler)
    {
        handlers.remove(handler);
    }
    
    public Iterator iterator()
    {
        return handlers.iterator();
    }
}