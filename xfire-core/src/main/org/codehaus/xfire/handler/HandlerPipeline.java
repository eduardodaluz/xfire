package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HandlerPipeline
{
    private List handlers;
    
    public HandlerPipeline()
    {
        handlers = new ArrayList();
    }
    
    public Handler getHandler(int i)
    {
        return (Handler) handlers.get(i);
    }
    
    public int size()
    {
        return handlers.size();
    }
    
    public void invoke(MessageContext context) 
    	throws Exception
    {
        for (int i = 0; i < size(); i++ )
        {
            Handler h = getHandler(i);
            try
            {
                h.invoke(context);
            }
            catch (Exception e)
            {
                h.handleFault(e, context);
                for ( i = i-1; i >= 0; i-- )
                {
                    h = getHandler(i);
                }
                
                throw e;
            }
        }
    }
    
    public void handleFault(Exception e, MessageContext context) 
    {
        for (int i = size(); i >= 0; i-- )
        {
            Handler h = getHandler(i);
            h.handleFault(e, context);
        }
    }
    
    public void addHandler(Handler handler)
    {
        handlers.add(handler);
    }
    
    public void remove(Handler handler)
    {
        handlers.remove(handler);
    }
    
    public Iterator iterator()
    {
        return handlers.iterator();
    }
}