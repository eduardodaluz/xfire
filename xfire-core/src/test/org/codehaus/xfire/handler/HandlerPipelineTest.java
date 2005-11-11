package org.codehaus.xfire.handler;

/**
 * @author Arjen Poutsma
 */

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;

public class HandlerPipelineTest
        extends TestCase
{
    private HandlerPipeline handlerPipeline;

    public void testPhases()
            throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        phases.add(new Phase(Phase.PARSE, 100));
        phases.add(new Phase(Phase.PRE_DISPATCH, 100));
        phases.add(new Phase(Phase.DISPATCH, 500));
        phases.add(new Phase(Phase.USER, 500));
        
        handlerPipeline = new HandlerPipeline(phases);

        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler handler2 = new PhaseHandler(Phase.PARSE);
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        
        handlerPipeline.invoke(new MessageContext());
        
        assertTrue(handler1.isInvoked());
        assertTrue(handler2.isInvoked());
    }
    
    public void testSorting()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        
        handlerPipeline = new HandlerPipeline(phases);

        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler2 handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler2.before(handler1.getClass().getName());
        
        assertEquals(1, handler1.compareTo(handler2));
        assertEquals(-1, handler2.compareTo(handler1));
        
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        handlerPipeline.sort();
        
        List handlers = handlerPipeline.getHandlers(Phase.TRANSPORT);
        
        assertTrue(handlers.get(0) == handler2);
        assertTrue(handlers.get(1) == handler1);
        
        // try reverse ordering
        handlerPipeline = new HandlerPipeline(phases);

        handler1 = new PhaseHandler(Phase.TRANSPORT);
        handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler2.after(handler1.getClass().getName());
        
        assertEquals(-1, handler1.compareTo(handler2));
        assertEquals(1, handler2.compareTo(handler1));
        
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        handlerPipeline.sort();

        handlers = handlerPipeline.getHandlers(Phase.TRANSPORT);
        
        assertTrue(handlers.get(0) == handler1);
        assertTrue(handlers.get(1) == handler2);
    }
    

    public void testInvalidSorting()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        
        handlerPipeline = new HandlerPipeline(phases);
    
        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler2 handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler1.before(handler2.getClass().getName());
        handler2.before(handler1.getClass().getName());
        
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        
        try
        {
            handlerPipeline.sort();
            fail("Invalid sort!");
        }
        catch (XFireRuntimeException e) {}
        
        handlerPipeline = new HandlerPipeline(phases);
        
        handler1 = new PhaseHandler(Phase.TRANSPORT);
        handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler1.after(handler2.getClass().getName());
        handler2.after(handler1.getClass().getName());
        
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        
        try
        {
            handlerPipeline.sort();
            fail("Invalid sort!");
        }
        catch (XFireRuntimeException e) {}
    }
        
    public class PhaseHandler extends AbstractHandler 
    {
        private String phase;
        private boolean invoked = false;
        
        public PhaseHandler(String phase)
        {
            this.phase = phase;
        }
        
        public void invoke(MessageContext context)
            throws Exception
        {
            invoked = true;
        }

        public boolean isInvoked()
        {
            return invoked;
        }

        public String getPhase()
        {
            return phase;
        }
    }
    
    public class PhaseHandler2 extends AbstractHandler 
    {
        private String phase;
        private boolean invoked = false;
        
        public PhaseHandler2(String phase)
        {
            this.phase = phase;
        }
        
        public void invoke(MessageContext context)
            throws Exception
        {
            invoked = true;
        }

        public boolean isInvoked()
        {
            return invoked;
        }

        public String getPhase()
        {
            return phase;
        }
    }
}