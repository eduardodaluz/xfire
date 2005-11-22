package org.codehaus.xfire.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HandlerPipeline
    extends AbstractHandler
    implements Handler
{
    private static final Log log = LogFactory.getLog(HandlerPipeline.class);
    
    private List phases;
    private Map handlers;

    public HandlerPipeline(List phases)
    {
        handlers = new HashMap();

        // Order the phases correctly based on priority
        Collections.sort(phases);
        this.phases = phases;
        
        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            handlers.put(phase.getName(), new HandlerOrderer());
        }
    }
    
    public void addHandlers(List newhandlers)
    {
        if (newhandlers == null) return;
        
        for (Iterator itr = newhandlers.iterator(); itr.hasNext();)
        {
            Handler handler = (Handler) itr.next();

            addHandler(handler);
        }
    }

    public void addHandler(Handler handler)
    {
        HandlerOrderer phaseHandlers = getPhase(handler.getPhase());
        
        if (phaseHandlers == null) 
            throw new XFireRuntimeException("Invalid phase: " + handler.getPhase());

        phaseHandlers.insertHandler(handler);
    }

    public HandlerOrderer getPhase(String phase)
    {
        return (HandlerOrderer) handlers.get(phase);
    }
    
    /**
     * Invokes each phase's handler in turn.
     * 
     * @param context
     * @throws Exception
     */
    public void invoke(MessageContext context) 
    	throws Exception
    {
        Stack invoked = new Stack();
        context.setProperty(this.toString(), invoked);
        
        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            List phaseHandlers = getPhase(phase.getName()).getHandlers();
            for (int i = 0; i < phaseHandlers.size(); i++ )
            {
                Handler h = (Handler) phaseHandlers.get(i);
                try
                {
                    log.debug("Invoking handler " + h.getClass().getName() + " in phase " + phase.getName());
                    
                    h.invoke(context);
                }
                finally
                {
                    // Add the invoked handler to the stack so we can come
                    // back to it later if a fault occurs.
                    invoked.push(h);
                }
            }
        }
    }
    
    /**
     * Takes a fault, creates a fault message and sends it via the fault channel.
     * 
     * @param fault
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context) 
    {
        Stack invoked = (Stack) context.getProperty(this.toString());

		if ( null != invoked )
		{
			while (invoked.size() > 0)
			{
				Handler h = (Handler) invoked.pop();
				h.handleFault(fault, context);
			}
		}
	}
    
    /**
     * Determines whether or not this Pipeline "understands" a particular header.
     * @param name
     * @return true if pipeline understands a header
     */
    public boolean understands(QName name)
    {
        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            List phaseHandlers = getPhase(phase.getName()).getHandlers();
            for (int i = 0; i < phaseHandlers.size(); i++ )
            {
                Handler h = (Handler) phaseHandlers.get(i);
                QName[] understoodQs = h.getUnderstoodHeaders();

                if (understoodQs != null)
                {
                    for (int j = 0; j < understoodQs.length; j++)
                    {
                        if (understoodQs[j].equals(name))
                            return true;
                    }
                }
            }
        }

        return false;
    }
}