package org.codehaus.xfire.client;

import java.lang.reflect.Method;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;

public class HeaderTest extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory factory = new ObjectServiceFactory(getTransportManager()) {
            protected boolean isHeader(Method method, int j)
            {
                if (j >= 0) return true;
                
                return super.isHeader(method, j);
            }
        };
        
        service = factory.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        getServiceRegistry().register(service);
    }
    
    public void testHeaders() throws Exception
    {
        XFireProxyFactory xpf = new XFireProxyFactory(getXFire());
        
        Echo echo = (Echo) xpf.create(service, "xfire.local://Echo");
        
        assertEquals("hi", echo.echo("hi"));
    }
}
