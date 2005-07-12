package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHandlerSupport
    implements HandlerSupport
{
    private List inHandlers;
    private List outHandlers;
    private List faultHandlers;

    public void addFaultHandler(Handler handler)
    {
        if (faultHandlers == null) faultHandlers = new ArrayList();
        
        faultHandlers.add(handler);
    }
    
    public List getFaultHandlers()
    {
        return faultHandlers;
    }

    public void setFaultHandlers(List faultHandlers)
    {
        this.faultHandlers = faultHandlers;
    }

    public void addInHandler(Handler handler)
    {
        if (inHandlers == null) inHandlers = new ArrayList();
        
        inHandlers.add(handler);
    }
    
    public List getInHandlers()
    {
        return inHandlers;
    }

    public void setInHandlers(List inHandlers)
    {
        this.inHandlers = inHandlers;
    }

    public void addOutHandler(Handler handler)
    {
        if (outHandlers == null) outHandlers = new ArrayList();
        
        outHandlers.add(handler);
    }
    
    public List getOutHandlers()
    {
        return outHandlers;
    }

    public void setOutHandlers(List outHandlers)
    {
        this.outHandlers = outHandlers;
    }
}
