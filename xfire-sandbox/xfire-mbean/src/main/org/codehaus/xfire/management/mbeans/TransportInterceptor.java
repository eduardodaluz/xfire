package org.codehaus.xfire.management.mbeans;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;

public class TransportInterceptor implements MethodInterceptor
{
    private ServiceStat serviceStat;
    private Transport transport;
    private Map<Object, Object> channels = new HashMap<Object, Object>();
    
    public TransportInterceptor(Transport transport, ServiceStat serviceStat)
    {
        super();

        this.transport = transport;
        this.serviceStat = serviceStat;
    }
    
    public Object intercept(Object t, Method m, Object[] args, MethodProxy proxy)
        throws Throwable
    {
        Object c = (Object) proxy.invoke(transport, args);

        if (m.getName().equals("createChannel"))
        {
            Object cached = channels.get(c);
            if (cached != null)
                return cached;
            
            ChannelInterceptor interceptor = new ChannelInterceptor((Channel) c, serviceStat);
            Enhancer e = new Enhancer();
            e.setSuperclass(c.getClass());
            e.setCallbackType(ChannelInterceptor.class);
            e.setUseFactory(false);

            Class mock = e.createClass();
            Enhancer.registerCallbacks(mock, new Callback[] { interceptor });
            
            try
            {
                cached = Sun14ReflectionProvider.newInstance(mock);
                channels.put(c, cached);
                return cached;
            }
            catch (Exception e1)
            {
                throw new XFireRuntimeException("Couldn't instantiate MBean transport.", e1);
            }
        }
        else if (m.getName().equals("close"))
        {
            channels.remove(c);
        }
        
        return c;
    }
}