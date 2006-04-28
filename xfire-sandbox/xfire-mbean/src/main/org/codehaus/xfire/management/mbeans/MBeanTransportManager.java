package org.codehaus.xfire.management.mbeans;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.Transport;

public class MBeanTransportManager extends DefaultTransportManager
{
    private ServiceStat serviceStat;

    public MBeanTransportManager(ServiceStat serviceStat)
    {
        super();
        
        this.serviceStat = serviceStat;
    }

    @Override
    public void register(Transport transport)
    {
        TransportInterceptor interceptor = new TransportInterceptor(transport, serviceStat);
        Enhancer e = new Enhancer();
        e.setSuperclass(transport.getClass());
        e.setCallbackType(TransportInterceptor.class);
        e.setUseFactory(false);

        Class mock = e.createClass();
        Enhancer.registerCallbacks(mock, new Callback[] { interceptor });
        
        try
        {
            Object t = Sun14ReflectionProvider.newInstance(mock);
            super.register((Transport)t);
        }
        catch (Exception e1)
        {
            throw new XFireRuntimeException("Couldn't instantiate MBean transport.", e1);
        }
    }
}
