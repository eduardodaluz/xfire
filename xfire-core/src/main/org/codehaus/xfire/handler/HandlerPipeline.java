package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HandlerPipeline
    extends AbstractHandler
    implements Handler
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
                context.setProperty(this, new Integer(i));
                
                throw e;
            }
        }
    }
    
    public void handleFault(XFireFault e, MessageContext context) 
    {
        int total = size();
        
        Integer exceptionPoint = (Integer) context.getProperty(this);
        if (exceptionPoint != null)
            total = exceptionPoint.intValue();
        
        for (int i = total; i >= 0; i-- )
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