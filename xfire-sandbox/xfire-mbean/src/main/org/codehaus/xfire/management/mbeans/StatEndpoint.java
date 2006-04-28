package org.codehaus.xfire.management.mbeans;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.transport.DefaultEndpoint;

public class StatEndpoint extends DefaultEndpoint
{
    private ServiceStat stats;
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        long now = System.currentTimeMillis();
        
        // TODO Auto-generated method stub
        super.onReceive(context, msg);
        
        // set the response type
        long res = System.currentTimeMillis() - now;
        stats.setLastResponseTime(new Long(res));
        stats.setTotalRequestCount();
    }
}
