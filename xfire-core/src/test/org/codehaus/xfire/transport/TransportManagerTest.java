package org.codehaus.xfire.transport;

import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;

public class TransportManagerTest
    extends AbstractXFireTest
{
    public void testTM() throws Exception
    {
        ServiceRegistry reg = new DefaultServiceRegistry();
        
        Service service = getServiceFactory().create(Echo.class);
        
        TransportManager tm = new DefaultTransportManager(reg);
        assertEquals(4, tm.getTransports().size()); // the local transport should be there
        
        HttpTransport transport = new HttpTransport();
        tm.register(transport);
        assertEquals(5, tm.getTransports().size());
        
        reg.register(service);
        assertTrue(tm.isEnabled(transport, service.getName()));

        assertEquals(5, tm.getTransports(service.getName()).size());
        
        tm.disable(transport, service.getName());
        assertFalse(tm.isEnabled(transport, service.getName()));
        
        tm.enable(transport, service.getName());
        assertTrue(tm.isEnabled(transport, service.getName()));
        
        tm.disableAll(service.getName());
        assertEquals(0, tm.getTransports(service.getName()).size());
        
        tm.enableAll(service.getName());
        assertEquals(5, tm.getTransports(service.getName()).size());
    }
    
    public void testHTTPLookup()
    {
        TransportManager tm = getXFire().getTransportManager();
        
        Transport t = tm.getTransportForUri("http://localhost");

        assertTrue(t instanceof SoapHttpTransport);
        
        t = tm.getTransportForUri("https://localhost");

        assertTrue(t instanceof SoapHttpTransport);
    }
}
