package org.codehaus.xfire.handler;

/**
 * @author Arjen Poutsma
 */

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.xfire.MessageContext;

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
}