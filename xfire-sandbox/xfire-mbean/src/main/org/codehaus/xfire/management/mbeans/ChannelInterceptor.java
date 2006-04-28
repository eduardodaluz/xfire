package org.codehaus.xfire.management.mbeans;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.DefaultFaultHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.transport.Channel;

public class ChannelInterceptor implements MethodInterceptor
{
    private ServiceStat serviceStat;
    private Channel channel;
    
    public ChannelInterceptor(Channel channel, ServiceStat serviceStat)
    {
        super();

        this.serviceStat = serviceStat;
        this.channel = channel;
    }

    public Object intercept(Object c, Method m, Object[] args, MethodProxy proxy)
        throws Throwable
    {
        Object retVal = null;
        
        if (m.getName().equals("receive"))
        {
            long now = System.currentTimeMillis();
            
            MessageContext context = (MessageContext) args[0];
            if (context.getFaultHandler() == null)
            {
                context.setFaultHandler(createFaultHandler());
            }
            
            retVal = proxy.invoke(channel, args);
            
            // set the response type
            long res = System.currentTimeMillis() - now;
            serviceStat.setLastResponseTime(new Long(res));
            serviceStat.setTotalRequestCount();
        }
        
        return retVal;
    }
    
    /**
     * Create a Fault Handler which increases the failed request count.
     */
    protected Handler createFaultHandler()
    {
        return new DefaultFaultHandler() {

            @Override
            public void invoke(MessageContext context)
                throws Exception
            {System.out.println("fault!!");
                serviceStat.setFailedRequestCount();
                
                super.invoke(context);
            }
            
        };
    }
    
}
