package org.codehaus.xfire.management.mbeans;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalChannel;
import org.codehaus.xfire.transport.local.LocalTransport;

public class ServiceStatTest extends AbstractXFireTest
{
    XFire xfire;
    ServiceStat serviceStat;
    
    public void testTransports() throws Exception
    {
        Transport t = getTransportManager().getTransport(LocalTransport.BINDING_ID);
        
        assertTrue( t instanceof LocalTransport );
        
        Channel channel = t.createChannel();
        
        assertTrue( channel instanceof LocalChannel );
    }
    
    public void testMessages() throws Exception
    {
        Service service = getServiceFactory().create(Echo.class);
        service.setInvoker(new BeanInvoker(new EchoImpl()));
        
        getServiceRegistry().register(service);
        
        for (int i = 0; i < 10; i++)
        {
            invokeService("Echo", "/org/codehaus/xfire/management/mbeans/echo11.xml");
        }
        
        assertEquals(10, serviceStat.getSuccessfulRequestCount().longValue());
        assertEquals(10, serviceStat.getTotalRequestCount().longValue());
        assertEquals(0, serviceStat.getFailedRequestCount().longValue());
        assertTrue(serviceStat.getMinResponseTime().longValue() >= 0);
        assertTrue(serviceStat.getMaxResponseTime().longValue() > 0);
     
        printNode(invokeService("Echo", "/org/codehaus/xfire/management/mbeans/echoMalformed.xml"));
        assertEquals(11, serviceStat.getTotalRequestCount().longValue());
        assertEquals(1, serviceStat.getFailedRequestCount().longValue());
        assertEquals(10, serviceStat.getSuccessfulRequestCount().longValue());
    }
    
    @Override
    protected XFire getXFire()
    {
        if (xfire == null)
        {
            serviceStat = new ServiceStat();
            
            MBeanTransportManager tm = new MBeanTransportManager(serviceStat);
            tm.initialize();
            
            xfire = new DefaultXFire(new DefaultServiceRegistry(), tm);
        }
        
        return xfire;
    }
    
}
