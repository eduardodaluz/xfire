package org.codehaus.xfire.transport;

import junit.framework.TestCase;

import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.TestHttpTransport;

public class TransportManagerTest
    extends TestCase
{
    public void testTM() throws Exception
    {
        ServiceRegistry reg = new DefaultServiceRegistry();
        
        TransportManager tm = new DefaultTransportManager(reg);
        assertEquals(1, tm.getTransports().size()); // the local transport should be there
        
        tm.register(new TestHttpTransport());
        assertEquals(2, tm.getTransports().size());
        
        ServiceFactory factory = new ObjectServiceFactory(tm, new MessageBindingProvider());
        Service service = factory.create(Echo.class);
        reg.register(service);
        
        assertEquals(2, tm.getTransports(service.getName()).size());
        
        tm.disableAll(service.getName());
        assertEquals(0, tm.getTransports(service.getName()).size());
        
        tm.enableAll(service.getName());
        assertEquals(2, tm.getTransports(service.getName()).size());
        
    }
}
