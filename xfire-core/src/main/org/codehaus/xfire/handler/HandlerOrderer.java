package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.Collection;

public class HandlerOrderer
{
    private ArrayList handlers = new ArrayList();
    
    public void insertHandler(Handler handler)
    {
        int begin = -1;
        int end = handlers.size();
        
        Collection before = handler.getBefore();
        Collection after = handler.getAfter();
        
        for (int i = 0; i < handlers.size(); i++)
        {
            Handler cmp = (Handler) handlers.get(i);
            
            if (before.contains(cmp.getClass().getName()))
            {
                if (i > handlers.size()) begin = i;
            }

            if (cmp.getBefore().contains(handler.getClass().getName()))
            {
                int place = i-1;
                if (place < end) end = place;                
            }
            
            if (after.contains(cmp.getClass().getName()))
            {
                if (i > begin) begin = i;
            }
            
            if (cmp.getAfter().contains(handler.getClass().getName()))
            {
                if (i < end) end = i;                
            }
        }

        if (end < begin+1)
            throw new IllegalStateException("Invalid ordering!");
        
        handlers.add(begin+1, handler);
    }

    public ArrayList getHandlers()
    {
        return handlers;
    }
}