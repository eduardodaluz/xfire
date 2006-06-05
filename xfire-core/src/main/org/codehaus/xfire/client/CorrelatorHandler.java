package org.codehaus.xfire.client;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.addressing.AddressingInHandler;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;

public class CorrelatorHandler extends AbstractHandler
{
    private static final Log log = LogFactory.getLog(Client.class);

    private List calls;
    private Correlator correlator;
    
    public CorrelatorHandler(List calls)
    {
        super();
        
        setPhase(Phase.PRE_DISPATCH);
        after(AddressingInHandler.class.getName());
        
        this.calls = calls;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        log.debug("Correlating context with ID " + context.getId());
        
        ClientCall call = correlator.correlate(context, calls);
        
        if (call == null)
        {
            log.info("No correlated call was found.");
            return;
        }
        
        if (context != call.getContext())
        {
            context.getExchange().setOperation(call.getContext().getExchange().getOperation());
            context.getExchange().setOutMessage(call.getContext().getExchange().getOutMessage());
        }
        
        if (call != null)
        {
            log.debug("Found correlated context with ID " + context.getId());
            context.getInPipeline().addHandler(new ClientReceiveHandler(call));
        }
    }

    public Correlator getCorrelator()
    {
        return correlator;
    }

    public void setCorrelator(Correlator correlator)
    {
        this.correlator = correlator;
    }
}
